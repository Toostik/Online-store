package org.example.events.carts;

import lombok.Builder;

import java.util.List;

@Builder
public record CartItemsAddedEvent(
        String eventId,
        Long cartId,
        Long userId,
        List<Long> productIds

) {
}