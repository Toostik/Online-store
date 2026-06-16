package org.example.events.inventory;


import java.math.BigDecimal;

public record InventoryCommittedEvent(

        String eventId,

        String correlationId,

        Long orderId,

        Long userId,

        BigDecimal amount

) {
}
