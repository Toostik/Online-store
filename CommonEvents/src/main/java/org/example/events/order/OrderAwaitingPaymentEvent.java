package org.example.events.order;

import java.math.BigDecimal;

public record OrderAwaitingPaymentEvent(

        String eventId,

        String correlationId,

        Long orderId

) {
}
