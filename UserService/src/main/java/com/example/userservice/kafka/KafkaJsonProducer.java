package com.example.userservice.kafka;

import com.example.userservice.dto.CurrentUserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaJsonProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaJsonProducer.class);

    private final KafkaTemplate<String, CurrentUserDto> kafkaTemplate;

    public KafkaJsonProducer(KafkaTemplate<String, CurrentUserDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(CurrentUserDto currentUserDto){
        LOGGER.info("Message sent -> {}", currentUserDto);
        kafkaTemplate.send("users-registered", currentUserDto);
    }
}
