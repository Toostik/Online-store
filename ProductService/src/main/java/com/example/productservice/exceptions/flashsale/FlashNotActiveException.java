package com.example.productservice.exceptions.flashsale;

public class FlashNotActiveException extends RuntimeException {
    public FlashNotActiveException(String message) {
        super(message);
    }
}
