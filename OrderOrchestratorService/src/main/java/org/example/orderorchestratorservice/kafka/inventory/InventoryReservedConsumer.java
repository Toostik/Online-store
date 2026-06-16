package org.example.orderorchestratorservice.kafka.inventory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.inventory.InventoryReservedEvent;
import org.example.orderorchestratorservice.service.saga.SagaService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.Acknowledgment;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryReservedConsumer {

    private final SagaService sagaService;

    @KafkaListener(
            topics = "product-service.inventory.reserved",
            groupId = "order-orchestrator-consumers-group",
            containerFactory = "inventoryReservedKafkaListenerContainerFactory"
    )
    public void consume(
            InventoryReservedEvent event,
            Acknowledgment ack
    ){

        try {

            log.info(
                    "INVENTORY_RESERVED_RECEIVED orderId={} eventId={}",
                    event.orderId(),
                    event.eventId()
            );

            sagaService.inventoryReserved(
                    event
            );

            ack.acknowledge();

        }
        catch (Exception e){

            log.error(
                    "INVENTORY_RESERVED_ERROR orderId={} eventId={}",
                    event.orderId(),
                    event.eventId(),
                    e
            );

            throw e;
        }

    }

}
