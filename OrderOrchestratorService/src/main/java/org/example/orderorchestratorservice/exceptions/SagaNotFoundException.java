package org.example.orderorchestratorservice.exceptions;

public class SagaNotFoundException extends RuntimeException {
    public SagaNotFoundException(String message) {
        super(message);
    }
}
