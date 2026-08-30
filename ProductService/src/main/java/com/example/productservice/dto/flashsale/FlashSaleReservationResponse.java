package com.example.productservice.dto.flashsale;

import com.example.productservice.entity.enums.ReservationStatus;

import java.time.Instant;
import java.util.UUID;

public record FlashSaleReservationResponse(
        UUID reservationKey,
        Instant expiresAt,
        ReservationStatus status
) {
}