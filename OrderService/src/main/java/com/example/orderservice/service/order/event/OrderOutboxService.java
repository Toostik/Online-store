package com.example.orderservice.service.order.event;

import com.example.orderservice.dao.event.OutboxEventRepository;
import com.example.orderservice.entity.event.OutboxEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.events.order.OrderCancelledEvent;
import org.example.events.order.OrderConfirmedEvent;
import org.example.events.order.OrderCreatedEvent;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderOutboxService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    private void saveEvent(
            String type,
            String aggregateId,
            Object payload
    ) {

        OutboxEvent outbox =
                OutboxEvent.builder()
                        .aggregateType("order")
                        .aggregateId(aggregateId)
                        .type(type)
                        .payload(
                                objectMapper.valueToTree(payload)
                        )
                        .build();

        repository.save(outbox);

    }

    public void publishCreated(OrderCreatedEvent event) {

        saveEvent(
                "order.created",
                event.orderId().toString(),
                event
        );
    }

    public void publishConfirmed(OrderConfirmedEvent event) {

        saveEvent(
                "order.confirmed",
                event.orderId().toString(),
                event
        );

    }

    public void publishCancelled(OrderCancelledEvent event) {

        saveEvent(
                "order.cancelled",
                event.orderId().toString(),
                event
        );

    }

}



