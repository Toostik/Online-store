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

    private final KafkaTemplate<Object, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    public KafkaJsonProducer(KafkaTemplate<Object, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(CurrentUserDto currentUserDto){
        LOGGER.info("Message sent -> {}", currentUserDto.toString());


        try {
            kafkaTemplate.send("users-registered", objectMapper.writeValueAsString(currentUserDto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
