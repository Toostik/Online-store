package com.example.orderservice.dto.product;

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
