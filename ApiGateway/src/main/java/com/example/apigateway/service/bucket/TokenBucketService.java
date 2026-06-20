package com.example.apigateway.service.bucket;

public interface TokenBucketService {
    Long consumeToken(
            String key,
            Integer capacity,
            Double refillRate
    );
}
