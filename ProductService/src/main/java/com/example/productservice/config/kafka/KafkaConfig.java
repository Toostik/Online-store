package com.example.productservice.config.kafka;

import com.example.productservice.kafka.InventoryReleaseRequestedConsumer;
import lombok.RequiredArgsConstructor;
import org.example.events.inventory.InventoryCommitRequestedEvent;
import org.example.events.inventory.InventoryReleaseRequestedEvent;
import org.example.events.inventory.InventoryReserveRequestedEvent;
import org.example.events.order.OrderCreatedEvent;
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
    public ConcurrentKafkaListenerContainerFactory<String, InventoryReserveRequestedEvent>
    inventoryReserveRequestedKafkaListenerContainerFactory() {

        return creator.create(InventoryReserveRequestedEvent.class);

    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryCommitRequestedEvent>
    inventoryCommitRequestedKafkaListenerContainerFactory() {

        return creator.create(InventoryCommitRequestedEvent.class);

    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryReleaseRequestedEvent>
    inventoryReleaseRequestedKafkaListenerContainerFactory() {

        return creator.create(InventoryReleaseRequestedEvent.class);

    }

}
