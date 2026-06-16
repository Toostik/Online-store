package com.example.orderservice.exceptions.product;

public class PricesEmptyException extends RuntimeException {
    public PricesEmptyException() {
        super("List of prices is empty");
    }
}
