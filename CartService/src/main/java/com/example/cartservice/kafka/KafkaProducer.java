package com.example.cartservice.kafka;

import com.example.cartservice.dto.CartDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate <String, CartDto> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, CartDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, CartDto cartDto){
        log.info("Cart sent for create order -> {}", cartDto.getId());
        kafkaTemplate.send(topic,cartDto);
    }
}
