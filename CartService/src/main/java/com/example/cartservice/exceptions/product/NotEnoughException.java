package com.example.cartservice.exceptions.product;

public class NotEnoughException extends RuntimeException {
    public NotEnoughException(Long productId) {
        super("Not enough stock for product " + productId);
    }
}
