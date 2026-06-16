package org.example.events.product;

public record ProductDeletedEvent(String eventId, Long productId) {
}
