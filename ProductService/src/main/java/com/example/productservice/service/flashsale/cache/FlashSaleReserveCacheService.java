package com.example.productservice.service.flashsale.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashSaleReserveCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Long> reserveFlashSaleScript;
    private final String RESERVATION_PREFIX = "flashsale:";

    public boolean isCacheReserved(Long flashSaleId, Integer quantity) {

        String key = RESERVATION_PREFIX + flashSaleId;

        try {

            Long result = redisTemplate.execute(
                    reserveFlashSaleScript,
                    Collections.singletonList(key),
                    quantity
            );

            return result != null && result >= 0;

        }
        catch (Exception e) {

            log.warn("Redis unavailable");

            return false;
        }
    }

    public void releaseReservation(Long flashSaleId, Integer quantity){

        String key = RESERVATION_PREFIX + flashSaleId;

        try {

           redisTemplate.opsForValue().increment(key, quantity);

        } catch (Exception e){
            log.warn("Redis unavailable");
        }

    }



}
