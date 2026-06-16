package com.example.notificationservice.service.event;

import com.example.notificationservice.dao.event.OutboxEventRepository;
import com.example.notificationservice.entity.event.OutboxEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.events.notification.NotificationOrderCancelledEvent;
import org.example.events.notification.NotificationOrderConfirmedEvent;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationOutboxService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    private void saveEvent(
            String type,
            String aggregateId,
            Object payload
    ) {

        OutboxEvent outbox =
                OutboxEvent.builder()
                        .aggregateType("notification")
                        .aggregateId(aggregateId)
                        .type(type)
                        .payload(
                                objectMapper.valueToTree(payload)
                        )
                        .build();

        repository.save(outbox);
    }

    public void publishOrderConfirmed(
            NotificationOrderConfirmedEvent event
    ) {

        saveEvent(
                "notification.order.confirmed",
                event.orderId().toString(),
                event
        );
    }

    public void publishOrderCancelled(
            NotificationOrderCancelledEvent event
    ) {

        saveEvent(
                "notification.order.cancelled",
                event.orderId().toString(),
                event
        );
    }

}
