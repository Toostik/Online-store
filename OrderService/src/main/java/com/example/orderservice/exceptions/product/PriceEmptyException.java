package com.example.orderservice.exceptions.product;

public class PriceEmptyException extends RuntimeException {
    public PriceEmptyException(Long id) {
        super("Price is null with id " + id);
    }
}
