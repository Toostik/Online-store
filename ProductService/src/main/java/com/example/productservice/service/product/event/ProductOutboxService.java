package com.example.productservice.service.product.event;

import com.example.productservice.dao.event.OutboxEventRepository;

import com.example.productservice.entity.event.OutboxEvent;
import com.example.productservice.entity.product.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.product.ProductCreatedEvent;
import org.example.events.product.ProductDeletedEvent;
import org.example.events.product.ProductPriceUpdatedEvent;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductOutboxService {

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

    public void publishCreated(Product product) {

        ProductCreatedEvent event =
                new ProductCreatedEvent(
                        UUID.randomUUID().toString(),
                        product.getId(),
                        product.getName(),
                        product.getPrice()
                );

        saveEvent(
                "product.created",
                product.getId().toString(),
                event
        );
    }

    public void publishDeleted(Long id) {

        ProductDeletedEvent event =
                new ProductDeletedEvent(
                        UUID.randomUUID().toString(),
                        id
                );

        saveEvent(
                "product.deleted",
                id.toString(),
                event
        );
    }

    public void publishPriceUpdated(Product product) {

        ProductPriceUpdatedEvent event =
                new ProductPriceUpdatedEvent(
                        UUID.randomUUID().toString(),
                        product.getId(),
                        product.getPrice()
                );

        saveEvent(
                "product.price.updated",
                product.getId().toString(),
                event
        );
    }


}
