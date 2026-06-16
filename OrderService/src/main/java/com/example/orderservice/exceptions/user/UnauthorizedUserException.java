package com.example.orderservice.exceptions.user;

public class UnauthorizedUserException extends RuntimeException {
    public UnauthorizedUserException() {
        super("Unauthorized user");
    }
}
