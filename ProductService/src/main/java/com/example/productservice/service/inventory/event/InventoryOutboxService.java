package com.example.productservice.service.inventory.event;

import com.example.productservice.dao.event.OutboxEventRepository;
import com.example.productservice.entity.event.OutboxEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.inventory.InventoryCommittedEvent;
import org.example.events.inventory.InventoryReleasedEvent;
import org.example.events.inventory.InventoryReservedEvent;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryOutboxService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    private void saveEvent(
            String type,
            String aggregateId,
            Object payload
    ) {

        OutboxEvent outbox =
                OutboxEvent.builder()
                        .aggregateType("product")
                        .aggregateId(aggregateId)
                        .type(type)
                        .payload(
                                objectMapper.valueToTree(payload)
                        )
                        .build();

        repository.save(outbox);

    }



    public void publishInventoryReserved(
            InventoryReservedEvent event
    ){

        saveEvent(
                "inventory.reserved",
                event.orderId().toString(),
                event
        );

    }

    public void publishInventoryReleased(
            InventoryReleasedEvent event
    ) {

        saveEvent(
                "inventory.released",
                event.orderId().toString(),
                event
        );

    }

    public void publishCommitted(
            InventoryCommittedEvent event
    ) {

        saveEvent(
                "inventory.committed",
                event.orderId().toString(),
                event
        );

    }
}
