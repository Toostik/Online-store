package com.example.paymentservice.service.event;

import com.example.paymentservice.dao.event.OutboxEventRepository;
import com.example.paymentservice.entity.event.OutboxEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.events.payment.PaymentCompletedEvent;
import org.example.events.payment.PaymentFailedEvent;
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


}
