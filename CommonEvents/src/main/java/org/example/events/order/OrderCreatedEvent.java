package org.example.events.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        String eventId,

        String correlationId,

        Long orderId,

        Long userId,

        BigDecimal totalAmount,

        List<OrderItemEvent> items,

        UUID reservationKey
) {
}
