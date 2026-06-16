package org.example.orderorchestratorservice.kafka.inventory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.inventory.InventoryCommittedEvent;
import org.example.orderorchestratorservice.service.saga.SagaService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryCommittedConsumer {

    private final SagaService sagaService;

    @KafkaListener(
            topics = "product-service.inventory.committed",
            groupId = "order-orchestrator-consumers-group",
            containerFactory = "inventoryCommittedKafkaListenerContainerFactory"
    )
    public void consume(
            InventoryCommittedEvent event,
            Acknowledgment ack
    ) {

        try {

            sagaService.inventoryCommitted(event);

            ack.acknowledge();

        } catch (Exception e) {

            throw e;
        }

    }

}
