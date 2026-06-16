package org.example.events.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderConfirmedEvent(
        String eventId,
        String correlationId,
        Long orderId,
        Long userId,
        BigDecimal amount
) {
}