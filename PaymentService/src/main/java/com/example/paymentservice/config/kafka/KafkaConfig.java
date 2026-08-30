package com.example.paymentservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.example.events.order.OrderAwaitingPaymentEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaFactoryCreator creator;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderAwaitingPaymentEvent>
    orderAwaitingPaymentKafkaListenerContainerFactory() {

        return creator.create(OrderAwaitingPaymentEvent.class);
    }


}
