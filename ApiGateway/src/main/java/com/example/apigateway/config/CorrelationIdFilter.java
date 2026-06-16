package com.example.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final String HEADER = "X-Request-ID";

    @Override
    public Mono<Void> filter(org.springframework.web.server.ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String requestId = exchange.getRequest().getHeaders().getFirst(HEADER);

        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        String finalRequestId = requestId;

        exchange.getRequest().mutate()
                .header(HEADER, finalRequestId)
                .build();

        MDC.put("requestId", finalRequestId);

        return chain.filter(exchange)
                .doFinally(signal -> MDC.clear());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}