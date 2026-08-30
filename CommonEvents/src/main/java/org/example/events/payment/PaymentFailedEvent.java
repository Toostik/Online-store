package org.example.events.payment;

import org.example.events.enums.PaymentFailureReason;

import java.math.BigDecimal;

public record PaymentFailedEvent(

        String eventId,

        String correlationId,

        Long orderId,

        Long userId,

        PaymentFailureReason reason

) {}
