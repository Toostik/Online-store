package com.example.cartservice.exceptions.cart;

public class CartExistsException extends RuntimeException {
    public CartExistsException() {
        super("Cart already exists");
    }
}
