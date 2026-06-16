package com.example.notificationservice.config.kafka;


import lombok.RequiredArgsConstructor;
import org.example.events.order.OrderCancelledEvent;
import org.example.events.order.OrderConfirmedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaFactoryCreator creator;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderConfirmedEvent>
    orderConfirmedKafkaListenerContainerFactory() {

        return creator.create(OrderConfirmedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCancelledEvent>
    orderCancelledKafkaListenerContainerFactory() {

        return creator.create(OrderCancelledEvent.class);
    }

}
