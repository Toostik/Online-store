package org.example.events.order;

public record OrderCancelledEvent(

        String eventId,

        String correlationId,

        Long orderId,

        Long userId,

        String reason

) {
}
