package org.example.orderorchestratorservice.service.saga;

import lombok.RequiredArgsConstructor;

import org.example.events.inventory.InventoryCommittedEvent;
import org.example.events.inventory.InventoryReleasedEvent;
import org.example.events.inventory.InventoryReservedEvent;
import org.example.events.order.OrderCreatedEvent;
import org.example.events.payment.PaymentCompletedEvent;
import org.example.events.payment.PaymentFailedEvent;
import org.example.orderorchestratorservice.service.saga.command.SagaCommandService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SagaService {

    private final SagaCommandService commandService;

    public void process(OrderCreatedEvent event) {
        commandService.process(event);
    }

    public void inventoryReserved(
            InventoryReservedEvent event
    ) {
        commandService.inventoryReserved(event);
    }

    public void paymentCompleted(
            PaymentCompletedEvent event
    ) {

        commandService.paymentCompleted(
                event
        );

    }

    public void paymentFailed(
            PaymentFailedEvent event
    ) {

        commandService.paymentFailed(event);

    }

    public void inventoryReleased(
            InventoryReleasedEvent event
    ) {

        commandService.inventoryReleased(event);

    }

    public void inventoryCommitted(InventoryCommittedEvent event){
        commandService.inventoryCommitted(event);
    }


}
