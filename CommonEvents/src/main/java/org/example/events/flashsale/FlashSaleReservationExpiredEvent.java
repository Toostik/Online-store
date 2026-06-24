package org.example.events.flashsale;

import lombok.Builder;

import java.util.UUID;
@Builder
public record FlashSaleReservationExpiredEvent(
        UUID reservationKey,
        Long flashSaleId,
        Long userId,
        Integer quantity
) {
}
