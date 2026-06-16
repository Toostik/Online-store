package com.example.productservice.kafka;

import com.example.productservice.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.inventory.InventoryReleaseRequestedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryReleaseRequestedConsumer {

    private final ProductService productService;

    @KafkaListener(
            topics = "order-orchestrator-service.inventory.release.requested",
            groupId = "products-consumers-group",
            containerFactory = "inventoryReleaseRequestedKafkaListenerContainerFactory"
    )
    public void consume(
            InventoryReleaseRequestedEvent event,
            Acknowledgment ack
    ) {

        try {

            log.info(
                    "INVENTORY_RELEASE_REQUEST_RECEIVED orderId={} eventId={}",
                    event.orderId(),
                    event.eventId()
            );

            productService.releaseInventory(event);

            ack.acknowledge();

            log.info(
                    "INVENTORY_RELEASE_REQUEST_PROCESSED orderId={} eventId={}",
                    event.orderId(),
                    event.eventId()
            );

        }
        catch (Exception e) {

            log.error(
                    "INVENTORY_RELEASE_REQUEST_ERROR orderId={} eventId={}",
                    event.orderId(),
                    event.eventId(),
                    e
            );

            throw e;
        }

    }

}
