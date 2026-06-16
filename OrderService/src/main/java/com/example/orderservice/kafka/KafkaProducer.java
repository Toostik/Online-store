package com.example.orderservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, String key, Object event) {

        log.info("KAFKA_SEND topic={} key={}", topic, key);

        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {

                    if (ex != null) {

                        log.error(
                                "Kafka send failed topic={} key={}",
                                topic,
                                key,
                                ex
                        );

                    } else {

                        log.info(
                                "Kafka send success topic={} partition={} offset={}",
                                topic,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );

                    }

                });
    }

}
