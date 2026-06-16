package com.example.cartservice.exceptions.product;

public class PricesEmptyException extends RuntimeException {
    public PricesEmptyException() {
        super("Prices list is empty");
    }
}
