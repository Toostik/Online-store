package com.example.paymentservice.dto.payment.event;

import java.math.BigDecimal;

public record PaymentRefundRequestedEvent(
        String eventId,
        String correlationId,
        Long orderId,
        Long userId,
        String paymentId,
        BigDecimal amount
) {}
