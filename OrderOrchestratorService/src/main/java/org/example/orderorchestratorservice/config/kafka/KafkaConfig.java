package org.example.orderorchestratorservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.example.events.inventory.InventoryCommittedEvent;
import org.example.events.inventory.InventoryReleasedEvent;
import org.example.events.inventory.InventoryReservedEvent;
import org.example.events.order.OrderCreatedEvent;
import org.example.events.payment.PaymentCompletedEvent;
import org.example.events.payment.PaymentFailedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaFactoryCreator creator;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent>
    orderCreatedKafkaListenerContainerFactory() {

        return creator.create(OrderCreatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryReservedEvent>
    inventoryReservedKafkaListenerContainerFactory() {

        return creator.create(InventoryReservedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent>
    paymentCompletedKafkaListenerContainerFactory() {

        return creator.create(PaymentCompletedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentFailedEvent>
    paymentFailedKafkaListenerContainerFactory() {

        return creator.create(
                PaymentFailedEvent.class
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryReleasedEvent>
    inventoryReleasedKafkaListenerContainerFactory() {

        return creator.create(
                InventoryReleasedEvent.class
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryCommittedEvent>
    inventoryCommittedKafkaListenerContainerFactory() {

        return creator.create(
                InventoryCommittedEvent.class
        );

    }


}
