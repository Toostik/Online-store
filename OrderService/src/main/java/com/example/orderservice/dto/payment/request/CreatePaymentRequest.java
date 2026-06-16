package com.example.orderservice.dto.payment.request;

import com.example.orderservice.entity.enums.PaymentMethod;
import com.example.orderservice.entity.enums.OrderStatus;

public record CreatePaymentRequest(
        PaymentMethod paymentMethod,
        String cardNumber,
        String expiryDate,
        Integer cvv,
        Long orderId,
        Long userId,
        OrderStatus orderStatus

) {
}
