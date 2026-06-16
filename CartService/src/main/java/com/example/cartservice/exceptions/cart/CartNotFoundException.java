package com.example.cartservice.exceptions.cart;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(Long userId) {
        super("Cart by user id " + userId + " not found");
    }
}
