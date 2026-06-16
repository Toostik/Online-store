package com.example.paymentservice.exceptions;

public class OrderPayedException extends RuntimeException {
    public OrderPayedException(String message) {
        super(message);
    }
}
