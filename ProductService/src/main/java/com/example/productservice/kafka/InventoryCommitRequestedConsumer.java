package com.example.productservice.kafka;

import com.example.productservice.service.inventory.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.inventory.InventoryCommitRequestedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryCommitRequestedConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(
            topics = "order-orchestrator-service.inventory.commit.requested",
            groupId = "products-consumers-group",
            containerFactory = "inventoryCommitRequestedKafkaListenerContainerFactory"
    )
    public void consume(
            InventoryCommitRequestedEvent event,
            Acknowledgment ack
    ) {

        try {

            log.info(
                    "INVENTORY_COMMIT_REQUEST_RECEIVED orderId={} eventId={}",
                    event.orderId(),
                    event.eventId()
            );

            inventoryService.commitInventory(event);

            ack.acknowledge();

            log.info(
                    "INVENTORY_COMMIT_REQUEST_PROCESSED orderId={} eventId={}",
                    event.orderId(),
                    event.eventId()
            );
        }
        catch (Exception e) {

            log.error(
                    "INVENTORY_COMMIT_REQUEST_ERROR orderId={} eventId={}",
                    event.orderId(),
                    event.eventId(),
                    e
            );

            throw e;
        }

    }

}
