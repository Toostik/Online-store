package com.example.productservice.dto.flashsale;

import org.example.events.order.DeliveryMethod;

public record CreateFlashSaleOrderRequest(
        String country,
        String city,
        String address,
        String apartment,
        String postalCode,
        DeliveryMethod deliveryMethod
) {
}
