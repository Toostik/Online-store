package com.example.authservice.exceptions.user;

public class PasswordWrongException extends RuntimeException {
    public PasswordWrongException(String message) {
        super(message);
    }
}
