package org.example.events.carts;

import lombok.Builder;

@Builder
public record CartDeletedEvent(

        String eventId,
        Long cartId

) {
}
