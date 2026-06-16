package org.example.orderorchestratorservice.service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.inventory.InventoryCommitRequestedEvent;
import org.example.events.inventory.InventoryReleaseRequestedEvent;
import org.example.events.inventory.InventoryReserveRequestedEvent;
import org.example.events.order.OrderAwaitingPaymentEvent;
import org.example.events.order.OrderCancelledEvent;
import org.example.events.order.OrderConfirmedEvent;
import org.example.events.payment.PaymentFailedEvent;
import org.example.orderorchestratorservice.dao.event.OutboxEventRepository;
import org.example.orderorchestratorservice.entity.event.OutboxEvent;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaOutboxService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    private void saveEvent(
            String type,
            String aggregateId,
            Object payload
    ) {

        OutboxEvent outbox =
                OutboxEvent.builder()
                        .aggregateType("saga")
                        .aggregateId(aggregateId)
                        .type(type)
                        .payload(
                                objectMapper.valueToTree(payload)
                        )
                        .build();

        repository.save(outbox);

    }

    public void publishInventoryReserveRequested(
            InventoryReserveRequestedEvent event
    ) {

        saveEvent(
                "inventory.reserve.requested",
                event.orderId().toString(),
                event
        );

    }

    public void publishOrderAwaitingPayment(
            OrderAwaitingPaymentEvent event
    ){

        saveEvent(
                "order.awaiting.payment",
                event.orderId().toString(),
                event
        );

    }
    public void publishConfirmed(
            OrderConfirmedEvent event
    ){

        saveEvent(
                "order.confirmed",
                event.orderId().toString(),
                event
        );

    }

    public void publishPaymentFailed(
            PaymentFailedEvent event
    ) {

        saveEvent(
                "payment.failed",
                event.orderId().toString(),
                event
        );

    }

    public void publishInventoryReleaseRequested(
            InventoryReleaseRequestedEvent event
    ) {

        saveEvent(
                "inventory.release.requested",
                event.orderId().toString(),
                event
        );

    }

    public void publishCancelled(
            OrderCancelledEvent event
    ) {

        saveEvent(
                "order.cancelled",
                event.orderId().toString(),
                event
        );

    }

    public void publishInventoryCommitRequest(
            InventoryCommitRequestedEvent event
    ) {

        saveEvent(
                "inventory.commit.requested",
                event.orderId().toString(),
                event
        );

    }

}
