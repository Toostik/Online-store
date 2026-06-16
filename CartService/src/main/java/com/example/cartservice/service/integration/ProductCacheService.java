package com.example.cartservice.service.integration;

import com.example.cartservice.dto.product.ProductDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public ProductDto get(Long id) {

        Object cached = redisTemplate.opsForValue()
                .get(RedisKeys.product(id));

        if (cached == null) {
            return null;
        }

        return objectMapper.convertValue(
                cached,
                ProductDto.class
        );
    }

    public void save(ProductDto product) {

        redisTemplate.opsForValue().set(
                RedisKeys.product(product.id()),
                product,
                Duration.ofHours(1)
        );

        redisTemplate.opsForValue().set(
                RedisKeys.productPrice(product.id()),
                product.price(),
                Duration.ofDays(1)
        );
    }

    public void delete(Long id) {

        redisTemplate.delete(RedisKeys.product(id));
        redisTemplate.delete(RedisKeys.productPrice(id));

    }

    public Map<Long, ProductDto> getProducts(List<Long> ids) {

        List<String> keys = ids.stream()
                .map(RedisKeys::product)
                .toList();

        List<Object> cachedValues =
                redisTemplate.opsForValue()
                        .multiGet(keys);

        Map<Long, ProductDto> products = new HashMap<>();

        if (cachedValues == null) {
            return products;
        }

        for (Object value : cachedValues) {

            if (value == null) {
                continue;
            }

            ProductDto dto = objectMapper.convertValue(
                    value,
                    ProductDto.class
            );

            products.put(dto.id(), dto);

        }

        return products;
    }

}