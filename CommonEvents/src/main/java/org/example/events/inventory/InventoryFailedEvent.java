package org.example.events.inventory;


import org.example.events.enums.InventoryFailureReason;
import org.example.events.order.OrderItemEvent;

import java.util.List;

public record InventoryFailedEvent(
        String eventId,
        String correlationId,
        Long orderId,
        InventoryFailureReason reason
) {}
