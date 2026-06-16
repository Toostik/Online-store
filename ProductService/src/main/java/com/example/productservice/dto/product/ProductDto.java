package com.example.productservice.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public record ProductDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String brand,
        String sku,
        Boolean stockStatus,
        BigDecimal averageRating,
        Integer reviewCount,
        String category,
        List<String> imageUrls) {
}
