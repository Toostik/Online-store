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

    private final ObjectMapper objectMapper = new ObjectMapper(); // для десериализации

    @KafkaListener(topics = "users-registered", groupId = "users-consumers-group")
    public void consume(String message, Acknowledgment ack) {
        try {
            // Преобразуем JSON в объект
            CurrentUserDto userDto = objectMapper.readValue(message, CurrentUserDto.class);

            LOGGER.info("Message received -> {}", userDto.toString());

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to deserialize message: {}", message, e);
            // можно сделать nack или просто пропустить
        } catch (Exception e) {
            LOGGER.error("Unexpected error while consuming message: {}", message, e);
        }
    }
}