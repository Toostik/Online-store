package com.example.orderservice.kafka;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.dto.request.OrderItemRequest;
import com.example.orderservice.entity.Order;
<<<<<<< HEAD
=======
import org.apache.kafka.clients.consumer.internals.Acknowledgements;
>>>>>>> feature/kafka-redis-logging
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
<<<<<<< HEAD
        LOGGER.info("Order created -> {}", orderDto.getId());
        kafkaTemplate.send(topic, orderDto);
=======
        kafkaTemplate.send(topic, orderDto);

>>>>>>> feature/kafka-redis-logging
    }
}
