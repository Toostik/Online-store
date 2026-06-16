package org.example.events.product;

import java.math.BigDecimal;

public record ProductCreatedEvent(
        String eventId,
        Long productId,
        String productName,
        BigDecimal price

) {
}
