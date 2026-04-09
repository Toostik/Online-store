package com.example.notificationservice.kafka;

import com.example.notificationservice.dto.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "orders-confirmed", groupId = "notification-consumers-group")
    public void consume(OrderDto orderDto, Acknowledgment ack){
        LOGGER.info("Order is confirmed -> {}", orderDto.getId());
        ack.acknowledge();
    }

}
