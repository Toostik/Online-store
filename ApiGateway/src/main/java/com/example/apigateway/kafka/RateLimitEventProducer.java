package com.example.apigateway.kafka;

import com.example.apigateway.dto.event.RateLimitExceededEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateLimitEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(
            RateLimitExceededEvent event) {

        kafkaTemplate.send(
                "rate-limit-violated",
                event.eventId().toString(),
                event
        );
    }

}
