package com.example.cartservice.kafka;

import com.example.cartservice.dto.cart.CartDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate <String, CartDto> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, CartDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, CartDto cartDto){
        String key = String.valueOf(cartDto.getId());
        cartDto.setEventId(UUID.randomUUID().toString());
        log.info("KAFKA_SEND_START topic={} key={}", topic, key);
        kafkaTemplate.send(topic, key, cartDto)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send message to topic {} with key {}",
                                topic, key, ex);
                    } else {
                        log.info("Message sent successfully to topic {} partition {} offset {}",
                                topic,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());

                    }
                });
    }
}
