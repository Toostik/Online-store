package org.example.events.inventory;
import org.example.events.order.OrderItemEvent;

import java.util.List;


public record InventoryReleasedEvent(
        String eventId,
        String correlationId,
        Long orderId
) {}
