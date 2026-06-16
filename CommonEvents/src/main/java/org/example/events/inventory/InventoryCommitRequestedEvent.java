package org.example.events.inventory;

import java.math.BigDecimal;
import java.util.List;

public record InventoryCommitRequestedEvent(

        String eventId,

        String correlationId,

        Long orderId,

        Long userId,

        BigDecimal amount

) {
}
