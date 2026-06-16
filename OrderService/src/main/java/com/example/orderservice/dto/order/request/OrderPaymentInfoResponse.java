package com.example.orderservice.dto.order.request;


import com.example.orderservice.entity.enums.OrderStatus;
import com.example.orderservice.entity.enums.PaymentMethod;

import java.math.BigDecimal;


public record OrderPaymentInfoResponse(

        Long orderId,

        Long userId,

        BigDecimal totalAmount,

        OrderStatus orderStatus

) {
}
