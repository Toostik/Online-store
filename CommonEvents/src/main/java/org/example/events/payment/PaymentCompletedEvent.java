package org.example.events.payment;

import java.math.BigDecimal;

public record PaymentCompletedEvent(
        String eventId,
        String correlationId,
        Long orderId,
        Long userId,
        BigDecimal amount,
        String paymentId
) {}
