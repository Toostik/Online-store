package com.example.orderservice.dto.order.request;

import org.example.events.order.DeliveryMethod;

public record CreateOrderRequest(
        String country,
        String city,
        String address,
        String apartment,
        String postalCode,
        DeliveryMethod deliveryMethod
) {
}
