package org.example.orderorchestratorservice.kafka.inventory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.inventory.InventoryReleasedEvent;
import org.example.orderorchestratorservice.service.saga.SagaService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryReleasedConsumer {

    private final SagaService sagaService;

    @KafkaListener(
            topics = "product-service.inventory.released",
            groupId = "order-orchestrator-consumers-group",
            containerFactory = "inventoryReleasedKafkaListenerContainerFactory"
    )
    public void consume(
            InventoryReleasedEvent event,
            Acknowledgment ack
    ) {

        try {

            sagaService.inventoryReleased(event);

            ack.acknowledge();

        }
        catch (Exception e) {

            throw e;
        }

    }

}
