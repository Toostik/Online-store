package com.example.cartservice.kafka;

import com.example.cartservice.dto.PriceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final RedisTemplate<String, Object> redisTemplate;

    @KafkaListener(topics = "product-price-updated", groupId = "cart-consumers-group")
    public void updatePrice(PriceDto priceDto, Acknowledgment ack){
        String key = "product:price:" + priceDto.getId();

        redisTemplate.opsForValue().set(key,priceDto.getNewPrice());

        ack.acknowledge();

        log.info("Price updated where id -> {}", priceDto.getId());
    }
}
