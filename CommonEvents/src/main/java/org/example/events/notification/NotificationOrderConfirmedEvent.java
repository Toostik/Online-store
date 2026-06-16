package org.example.events.notification;

import java.math.BigDecimal;

public record NotificationOrderConfirmedEvent(
        String eventId,
        String correlationId,
        Long orderId,
        Long userId,
        String email,
        String username,
        BigDecimal totalAmount
) {}
