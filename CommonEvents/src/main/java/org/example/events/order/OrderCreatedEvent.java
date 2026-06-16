package org.example.events.order;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreatedEvent(
        String eventId,

        String correlationId,

        Long orderId,

        Long userId,

        BigDecimal totalAmount,

        List<OrderItemEvent> items
) {
}
