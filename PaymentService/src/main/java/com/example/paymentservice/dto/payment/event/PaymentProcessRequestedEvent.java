package com.example.paymentservice.dto.payment.event;

import java.math.BigDecimal;

public record PaymentProcessRequestedEvent(
        String eventId,
        String correlationId,
        Long orderId,
        Long userId,
        BigDecimal amount,
        String paymentMethod
) {}
