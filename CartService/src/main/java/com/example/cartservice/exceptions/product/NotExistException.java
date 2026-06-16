package com.example.cartservice.exceptions.product;

public class NotExistException extends RuntimeException {
    public NotExistException(String message) {
        super(message);
    }
}
