package com.example.cartservice.service.cart.event;

import com.example.cartservice.dao.event.OutboxEventRepository;
import com.example.cartservice.entity.cart.Cart;
import com.example.cartservice.entity.event.EventType;
import com.example.cartservice.entity.event.OutboxEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.events.carts.CartClearedEvent;
import org.example.events.carts.CartCreatedEvent;
import org.example.events.carts.CartDeletedEvent;
import org.example.events.carts.CartItemsAddedEvent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartOutboxService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    private void saveEvent(
            EventType type,
            String aggregateId,
            Object payload
    ) {

        OutboxEvent event =
                OutboxEvent.builder()
                        .aggregateType("cart")
                        .aggregateId(aggregateId)
                        .type(type)
                        .payload(
                                objectMapper.valueToTree(payload)
                        )
                        .build();

        repository.save(event);

    }

    public void publishCreated(Cart cart) {

        CartCreatedEvent event =
                new CartCreatedEvent(
                        UUID.randomUUID().toString(),
                        cart.getId(),
                        cart.getUserId()
                );

        saveEvent(
                EventType.CART_CREATED,
                cart.getId().toString(),
                event
        );
    }

    public void publishItemsAdded(
            Cart cart,
            List<Long> productIds
    ) {

        CartItemsAddedEvent event =
                new CartItemsAddedEvent(
                        UUID.randomUUID().toString(),
                        cart.getId(),
                        cart.getUserId(),
                        productIds
                );

        saveEvent(
                EventType.CART_ITEMS_ADDED,
                cart.getId().toString(),
                event
        );
    }

    public void publishCleared(Cart cart) {

        CartClearedEvent event =
                new CartClearedEvent(
                        UUID.randomUUID().toString(),
                        cart.getId()
                );

        saveEvent(
                EventType.CART_CLEARED,
                cart.getId().toString(),
                event
        );
    }

    public void publishDeleted(Long cartId) {

        CartDeletedEvent event =
                new CartDeletedEvent(
                        UUID.randomUUID().toString(),
                        cartId
                );

        saveEvent(
                EventType.CART_DELETED,
                cartId.toString(),
                event
        );
    }

}