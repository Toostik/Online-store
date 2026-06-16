package com.example.paymentservice.entity.enums;

public enum OrderStatus {
    CREATED,
    RESERVING_ITEMS,
    AWAITING_PAYMENT,
    PAYMENT_PROCESSING,
    PAID,
    CONFIRMED,
    PROCESSING,
    PARTIALLY_SHIPPED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    FAILED
}
