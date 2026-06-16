package com.example.paymentservice.dto.payment.request;

import com.example.paymentservice.entity.enums.PaymentMethod;

public record CreatePaymentRequest(
        PaymentMethod paymentMethod,
        String cardNumber,
        String expiryDate,
        Integer cvv
) {
}
