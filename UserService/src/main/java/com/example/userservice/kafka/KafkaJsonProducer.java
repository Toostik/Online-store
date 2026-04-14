package com.example.userservice.kafka;

import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaJsonProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaJsonProducer.class);

    private final KafkaTemplate<String, UserDto> kafkaTemplate;

    public KafkaJsonProducer(KafkaTemplate<String, UserDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(UserDto userDto){
        LOGGER.info("Message sent -> {}", userDto);
        kafkaTemplate.send("users-registered", userDto);
    }
}
