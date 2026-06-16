package com.example.cartservice.exceptions.cart;

public class CartHasNotProductsException extends RuntimeException {
    public CartHasNotProductsException(String message) {
        super(message);
    }
}
