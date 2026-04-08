package com.example.authservice.kafka;

import com.example.authservice.dto.CurrentUserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaJsonConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaJsonConsumer.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "users-registered", groupId = "users-consumers-group")
    public void consume(CurrentUserDto userDto, Acknowledgment ack) {
        LOGGER.info("Received user -> {}", userDto);
        ack.acknowledge();
    }
}