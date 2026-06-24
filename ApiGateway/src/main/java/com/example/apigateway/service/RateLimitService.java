package com.example.apigateway.service;

import com.example.apigateway.dto.BucketConfig;
import com.example.apigateway.dto.ErrorResponse;
import com.example.apigateway.dto.RateLimitContext;
import com.example.apigateway.dto.event.RateLimitExceededEvent;
import com.example.apigateway.kafka.RateLimitEventProducer;
import com.example.apigateway.service.bucket.BucketConfigResolver;
import com.example.apigateway.service.bucket.RedisTokenBucketService;
import com.example.apigateway.service.metrics.RateLimitMetricsService;
import com.example.apigateway.service.whitelist.WhitelistService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {

    private final ObjectMapper objectMapper;
    private final BucketConfigResolver bucketConfigResolver;
    private final RedisTokenBucketService redisTokenBucketService;
    private final WhitelistService whitelistService;
    private final RateLimitMetricsService rateLimitMetricsService;
    private final RateLimitEventProducer rateLimitEventProducer;

    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        return exchange.getPrincipal()
                .cast(Authentication.class)
                .map(Authentication::getName)
                .defaultIfEmpty("ANONYMOUS")
                .flatMap(id -> {

                    if (!"ANONYMOUS".equals(id)) {

                        if (whitelistService.isWhitelisted(
                                "whitelist:user:" + id)) {

                            return chain.filter(exchange);
                        }

                        return applyLimit(
                                "rate_limit:user:" + id,
                                new RateLimitContext(
                                        id,
                                        null,
                                        exchange.getRequest()
                                                .getPath()
                                                .value()),
                                exchange,
                                chain
                        );
                    }

                    String ip = exchange.getRequest()
                            .getRemoteAddress()
                            .getAddress()
                            .getHostAddress();

                    if (whitelistService.isWhitelisted(
                            "whitelist:ip:" + ip)) {

                        return chain.filter(exchange);
                    }

                    return applyLimit(
                            "rate_limit:ip:" + ip,
                            new RateLimitContext(
                                    null,
                                    ip,
                                    exchange.getRequest()
                                            .getPath()
                                            .value()),
                            exchange,
                            chain
                    );
                });
    }


    private Mono<Void> applyLimit(
            String key,
            RateLimitContext context,
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        BucketConfig bucketConfig = bucketConfigResolver.resolve(exchange);

        Integer limit = bucketConfig.capacity();
        Double refillRate = bucketConfig.refillRate();

        Long tokensLeft =
                redisTokenBucketService.consumeToken(
                        key,
                        limit,
                        refillRate
                );

        exchange.getResponse()
                .getHeaders()
                .set("X-RateLimit-Limit", String.valueOf(limit));

        exchange.getResponse()
                .getHeaders()
                .set("X-RateLimit-Remaining", String.valueOf(tokensLeft));

        if (tokensLeft < 0) {

            try {
                return tooManyRequests(
                        context,
                        exchange,
                        limit
                );
            } catch (JsonProcessingException e) {
                log.error("Json processing error", e);
                throw new RuntimeException(e);
            }

        }

        return chain.filter(exchange);
    }


    private Mono<Void> tooManyRequests(
            RateLimitContext context,
            ServerWebExchange exchange,
            Integer limit) throws JsonProcessingException {

        long errorStart = System.currentTimeMillis();

        ErrorResponse response = ErrorResponse.builder()
                .status(429)
                .error("Too Many Requests")
                .message("Rate limit exceeded")
                .limit(limit)
                .build();

        byte[] bytes = objectMapper.writeValueAsBytes(response);

        exchange.getResponse()
                .setStatusCode(HttpStatus.TOO_MANY_REQUESTS);

        exchange.getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(bytes);

        rateLimitMetricsService.incrementExceeded();

//
//        rateLimitEventProducer.send(
//                RateLimitExceededEvent.builder()
//                        .eventId(UUID.randomUUID())
//                        .userId(context.userId())
//                        .ip(context.ip())
//                        .endpoint(
//                                context.endpoint()
//                        )
//                        .limit(limit)
//                        .timestamp(Instant.now())
//                        .build()
//        );

        log.info(
                "429_RESPONSE {} ms",
                System.currentTimeMillis() - errorStart
        );

        return exchange.getResponse()
                .writeWith(Mono.just(buffer));
    }

}
