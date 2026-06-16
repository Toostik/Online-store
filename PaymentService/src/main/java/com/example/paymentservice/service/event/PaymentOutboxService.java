package com.example.paymentservice.service.event;

import com.example.paymentservice.dao.event.OutboxEventRepository;
import com.example.paymentservice.dto.payment.event.*;
import com.example.paymentservice.entity.event.OutboxEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentOutboxService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    private void saveEvent(
            String type,
            String aggregateId,
            Object payload
    ) {

        OutboxEvent outbox =
                OutboxEvent.builder()
                        .aggregateType("payment")
                        .aggregateId(aggregateId)
                        .type(type)
                        .payload(
                                objectMapper.valueToTree(payload)
                        )
                        .build();

        repository.save(outbox);
    }

    public void publishProcessRequested(
            PaymentProcessRequestedEvent event
    ) {

        saveEvent(
                "payment.process.requested",
                event.orderId().toString(),
                event
        );
    }

    public void publishCompleted(
            PaymentCompletedEvent event
    ) {

        saveEvent(
                "payment.completed",
                event.orderId().toString(),
                event
        );
    }

    public void publishFailed(
            PaymentFailedEvent event
    ) {

        saveEvent(
                "payment.failed",
                event.orderId().toString(),
                event
        );
    }

    public void publishRefundRequested(
            PaymentRefundRequestedEvent event
    ) {

        saveEvent(
                "payment.refund.requested",
                event.orderId().toString(),
                event
        );
    }

    public void publishRefunded(
            PaymentRefundedEvent event
    ) {

        saveEvent(
                "payment.refunded",
                event.orderId().toString(),
                event
        );
    }

}
