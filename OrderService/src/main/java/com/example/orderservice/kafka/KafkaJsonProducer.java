package com.example.orderservice.kafka;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.dto.request.OrderItemRequest;
import com.example.orderservice.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KafkaJsonProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaJsonProducer.class);

    private final KafkaTemplate<String, OrderDto> kafkaTemplate;

    public KafkaJsonProducer(KafkaTemplate<String, OrderDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, OrderDto orderDto){
        kafkaTemplate.send(topic, orderDto);
    }
}
