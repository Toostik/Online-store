package com.example.productservice.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceDto {
    private Long id;
    private BigDecimal newPrice;
    private String eventId;

    public PriceDto(Long id, BigDecimal newPrice) {
        this.id = id;
        this.newPrice = newPrice;
    }
}
