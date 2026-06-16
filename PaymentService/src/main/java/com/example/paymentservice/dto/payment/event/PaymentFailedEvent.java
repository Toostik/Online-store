package com.example.paymentservice.dto.payment.event;

import com.example.paymentservice.entity.enums.FailureReason;
import com.example.paymentservice.entity.enums.PaymentFailureReason;

import java.math.BigDecimal;

public record PaymentFailedEvent(
        String eventId,

        String correlationId,

        Long orderId,

        Long userId,

        FailureReason reason
) {}
