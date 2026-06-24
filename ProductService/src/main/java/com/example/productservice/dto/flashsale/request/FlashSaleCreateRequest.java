package com.example.productservice.dto.flashsale.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record FlashSaleCreateRequest(

        BigDecimal discountedPrice,

        Integer quantity,

        Instant startsAt,

        Instant endsAt

) {
}
