package com.example.apigateway.service.bucket;

import com.example.apigateway.service.metrics.RateLimitMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTokenBucketService implements TokenBucketService {

    private final RateLimitMetricsService rateLimitMetricsService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Long> tokenBucketScript;
    private final LocalTokenBucketService localTokenBucketService;

    @Override
    public Long consumeToken(String key, Integer capacity, Double refillRate) {

        Long now = Instant.now().getEpochSecond();

        try {
            return redisTemplate.execute(
                    tokenBucketScript,
                    List.of(key),
                    capacity,
                    refillRate,
                    now
            );

        } catch (Exception ex) {


            rateLimitMetricsService.incrementFallback();

            log.warn(
                    "Redis unavailable, using Caffeine fallback"
            );

            return localTokenBucketService.consumeToken(
                    key,
                    capacity,
                    refillRate
            );
        }


    }
}
