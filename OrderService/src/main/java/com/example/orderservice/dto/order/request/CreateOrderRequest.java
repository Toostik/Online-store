package com.example.orderservice.dto.order.request;

import com.example.orderservice.entity.enums.DeliveryMethod;
import com.example.orderservice.entity.enums.PaymentMethod;

public record CreateOrderRequest(
        //address
        String country,
        String city,
        String address,
        String apartment,
        String postalCode,
        DeliveryMethod deliveryMethod,
        PaymentMethod paymentMethod,
        String cardNumber,
        String expiryDate,
        Integer cvv
) {
}
