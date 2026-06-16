package org.example.events.carts;

import lombok.Builder;

@Builder
public record CartClearedEvent(
        String eventId,
        Long cartId

) {
}