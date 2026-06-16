package com.example.userservice.kafka;

import com.example.userservice.dto.user.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, UserDto> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, UserDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, UserDto userDto){
        String key = String.valueOf(userDto.getId());
        userDto.setEventId(UUID.randomUUID().toString());
        log.info("KAFKA_SEND_START topic={} key={}", topic, key);
        kafkaTemplate.send(topic, key, userDto)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send message to topic {} with key {}",
                                topic, key, ex);
                    } else {
                        log.info("Message sent successfully to topic {} partition {} offset {}",
                                topic,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
