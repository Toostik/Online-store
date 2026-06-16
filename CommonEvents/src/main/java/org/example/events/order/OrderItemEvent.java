package org.example.events.order;

public record OrderItemEvent(Long productId, Integer stockQuantity) {
}
