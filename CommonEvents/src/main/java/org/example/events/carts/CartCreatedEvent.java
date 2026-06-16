package org.example.events.carts;

import lombok.Builder;

@Builder
public record CartCreatedEvent(
        String eventId,
        Long cartId,
        Long userId
) {
}