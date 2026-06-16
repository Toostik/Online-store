package org.example.events.product;

public record ProductStockChangedEvent(String eventId, Long productId, Integer stockQuantity) {
}
