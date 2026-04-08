package com.example.orderservice.entity;

public enum Status {
    CREATED,          // создан
    CONFIRMED,        // подтверждён
    PROCESSING,       // в обработке
    PARTIALLY_SHIPPED,// частично отгружен
    SHIPPED,          // полностью отгружен
    DELIVERED, // доставлен
    CANCELLED
}
