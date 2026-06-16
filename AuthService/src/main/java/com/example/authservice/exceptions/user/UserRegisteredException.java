package com.example.authservice.exceptions.user;

public class UserRegisteredException extends RuntimeException {
    public UserRegisteredException(String message) {
        super(message);
    }
}
