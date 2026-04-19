package com.example.cartservice.exceptions;

public class NotExistException extends RuntimeException {
    public NotExistException(String message) {
        super(message);
    }
}
