package com.example.cartservice.service.cart.cache;

import com.example.cartservice.dto.cart.CartResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CartCacheService {

    private static final Duration TTL = Duration.ofHours(1);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public CartResponse get(Long userId) {

        Object cached = redisTemplate.opsForValue()
                .get(RedisKeys.cart(userId));

        if (cached == null) {
            return null;
        }

        return objectMapper.convertValue(
                cached,
                CartResponse.class
        );
    }

    public void save(
            Long userId,
            CartResponse response
    ) {

        redisTemplate.opsForValue().set(
                RedisKeys.cart(userId),
                response,
                TTL
        );

    }

    public void delete(Long userId) {

        redisTemplate.delete(
                RedisKeys.cart(userId)
        );

    }

}