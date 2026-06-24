package org.example.events.flashsale;

import org.example.events.order.AddressDto;
import org.example.events.order.DeliveryMethod;

import java.math.BigDecimal;
import java.util.UUID;

public record FlashSaleReservationAndCheckoutEvent(
        UUID eventId,
        UUID reservationKey,
        Long flashSaleId,
        Long userId,
        Long productId,
        Integer quantity,
        BigDecimal discountedPrice,
        AddressDto address,
        DeliveryMethod deliveryMethod
) {
}
