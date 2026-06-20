package com.example.apigateway.service.bucket;

import com.example.apigateway.dto.LocalBucket;
import com.example.apigateway.service.metrics.RateLimitMetricsService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LocalTokenBucketService {

    private final RateLimitMetricsService rateLimitMetricsService;

    private final Cache<String, LocalBucket> cache =
            Caffeine.newBuilder()
                    .expireAfterAccess(Duration.ofHours(1))
                    .build();

    public Long consumeToken(
            String key,
            Integer capacity,
            Double refillRate) {

        long now = Instant.now().getEpochSecond();

        LocalBucket bucket = cache.get(
                key,
                k -> new LocalBucket(
                        capacity,
                        now
                )
        );

        long elapsed = now - bucket.getLastRefill();

        int refill =
                (int) Math.floor(
                        elapsed * refillRate
                );

        int tokens =
                Math.min(
                        capacity,
                        bucket.getTokens() + refill
                );

        if (tokens <= 0) {

            bucket.setTokens(0);
            bucket.setLastRefill(now);

            return -1L;
        }

        tokens--;

        bucket.setTokens(tokens);
        bucket.setLastRefill(now);


        return (long) tokens;
    }
}
