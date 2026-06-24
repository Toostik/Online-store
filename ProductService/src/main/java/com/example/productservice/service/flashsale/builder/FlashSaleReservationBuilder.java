package com.example.productservice.service.flashsale.builder;

import com.example.productservice.entity.flashsale.FlashSale;
import com.example.productservice.entity.flashsale.FlashSaleReservation;
import com.example.productservice.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class FlashSaleReservationBuilder {

    public FlashSaleReservation create(FlashSale flashSale, Integer quantity, Long userId){
        return FlashSaleReservation
                .builder()
                .flashSale(flashSale)
                .userId(userId)
                .quantity(quantity)
                .reservedPrice(flashSale.getDiscountedPrice())
                .expiresAt(Instant.now().plus(10, ChronoUnit.MINUTES))
                .build();
    }
}
