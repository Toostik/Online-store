package com.example.orderservice.kafka;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.dto.request.OrderItemRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KafkaJsonProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaJsonProducer.class);

    private final KafkaTemplate<String, Map<Long, Integer>> kafkaTemplate;

    public KafkaJsonProducer(KafkaTemplate<String, Map<Long, Integer>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(OrderDto orderDto, Map<Long, Integer> quantityOfProducts){
        LOGGER.info("Order created -> {}", orderDto.getId());
        kafkaTemplate.send("orders-created", quantityOfProducts);
    }
}
