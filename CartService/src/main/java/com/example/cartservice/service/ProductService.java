package com.example.cartservice.service;

import com.example.cartservice.dto.request.CheckProductRequest;
import com.example.cartservice.dto.request.CheckProductResponse;
import com.example.cartservice.feign.ProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductClient productClient;

    public Map<Long, BigDecimal> getPrices(List<Long> ids){
        Map<Long, BigDecimal> prices = new HashMap<>();
        String key;
        BigDecimal price;
        for(Long id: ids){
            key = "product:price:" + id;
            price = (BigDecimal) redisTemplate.opsForValue().get(key);

            if(price != null){
                prices.put(id, price);
            } else {
                price = productClient.getPrice(id);
                prices.put(id, price);
                redisTemplate.opsForValue().set(key,price, Duration.ofDays(1));
            }
        }
        return prices;
    }
    public CheckProductResponse checkAvailability(CheckProductRequest request){
        return productClient.getProductsAvailability(request);
    }

}
