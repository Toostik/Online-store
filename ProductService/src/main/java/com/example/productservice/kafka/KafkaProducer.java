package com.example.productservice.kafka;

import com.example.productservice.dto.PriceDto;
import com.example.productservice.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, PriceDto> kafkaTemplate;

    public void sendMessage(String topic, PriceDto priceDto){
        kafkaTemplate.send(topic, priceDto);
        log.info("New price received. Product id -> {}", priceDto.getId());
    }
}
