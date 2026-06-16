package com.example.cartservice.kafka;

import com.example.cartservice.dao.event.ProcessedEventRepository;
import com.example.cartservice.dto.product.PriceDto;
import com.example.cartservice.entity.event.ProcessedEvent;
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
    private final ProcessedEventRepository processedEventRepository;
    @KafkaListener(topics = "product-price-updated", groupId = "cart-consumers-group")
    public void updatePrice(PriceDto priceDto, Acknowledgment ack){

        try {
            log.info("KAFKA_PRICE_EVENT_RECEIVED id={}", priceDto.getId());

            if (processedEventRepository.existsById(priceDto.getEventId())) {
                log.warn("EVENT_ALREADY_PROCESSED id={}", priceDto.getEventId());
                ack.acknowledge();
                return;
            }

            String key = "product:price:" + priceDto.getId();

            redisTemplate.opsForValue().set(key,priceDto.getNewPrice());

            processedEventRepository.save(new ProcessedEvent(priceDto.getEventId()));

            ack.acknowledge();

            log.info("Price updated where id -> {}", priceDto.getId());

        }catch (Exception e){
            log.error("KAFKA_NOTIFICATION_ERROR orderId={} eventId={}",
                    priceDto.getId(), priceDto.getEventId(), e);
            throw e;
        }
    }
}
