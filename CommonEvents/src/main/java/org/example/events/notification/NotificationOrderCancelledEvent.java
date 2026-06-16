package org.example.events.notification;

public record NotificationOrderCancelledEvent(
        String eventId,
        String correlationId,
        Long orderId,
        Long userId,
        String email,
        String username,
        String reason
) {}