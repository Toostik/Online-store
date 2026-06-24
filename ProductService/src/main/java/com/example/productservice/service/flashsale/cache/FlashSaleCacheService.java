package com.example.productservice.service.flashsale.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashSaleCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String RESERVATION_PREFIX = "flashsale:";

    public void save(Long flashSaleId, Integer totalQuantity){

        String key = RESERVATION_PREFIX + flashSaleId;

        try{
            redisTemplate.opsForValue().set(key, totalQuantity);
        }catch (Exception e){
            log.error("Redis unavailable", e);
        }

    }

    public Integer get(Long flashSaleId) {
        String key = RESERVATION_PREFIX + flashSaleId;

        try{

            return (Integer) redisTemplate.opsForValue().get(key);

        }catch (Exception e){
            log.error("Redis unavailable", e);
        }

        return null;

    }
}
