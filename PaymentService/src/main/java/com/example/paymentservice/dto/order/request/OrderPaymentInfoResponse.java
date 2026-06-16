package com.example.paymentservice.dto.order.request;

import com.example.paymentservice.entity.enums.OrderStatus;

import java.math.BigDecimal;

public record OrderPaymentInfoResponse(

        Long orderId,

        Long userId,

        BigDecimal totalAmount,

        OrderStatus status

) {
}
