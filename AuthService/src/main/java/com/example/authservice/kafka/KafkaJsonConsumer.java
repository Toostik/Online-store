package com.example.authservice.kafka;

import com.example.authservice.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaJsonConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaJsonConsumer.class);

    @KafkaListener(topics = "users-registered", groupId = "users-consumers-group")
    public void consume(UserDto user, Acknowledgment ack) {
        LOGGER.info("Received user -> {}", user);
        ack.acknowledge();
    }
}