package com.example.cartservice.exceptions;

public class CartHasNotProductsException extends RuntimeException {
    public CartHasNotProductsException(String message) {
        super(message);
    }
}
