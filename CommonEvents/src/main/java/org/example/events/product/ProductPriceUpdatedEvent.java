package org.example.events.product;

import java.math.BigDecimal;

public record ProductPriceUpdatedEvent(
        String eventId,
        Long productId,
        BigDecimal price
) {
}
