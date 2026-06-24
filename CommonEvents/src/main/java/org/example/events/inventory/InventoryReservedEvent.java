package org.example.events.inventory;



import org.example.events.order.OrderItemEvent;

import java.util.List;
import java.util.UUID;

public record InventoryReservedEvent(
        String eventId,
        String correlationId,
        Long orderId,
        List<OrderItemEvent> items
) {}