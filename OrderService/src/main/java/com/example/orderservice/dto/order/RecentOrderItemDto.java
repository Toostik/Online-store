package com.example.orderservice.dto.order;

import com.example.orderservice.entity.enums.ItemShipmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RecentOrderItemDto(
        Long productId,
        String productName,
        String imagePath,
        Integer quantity,
        BigDecimal price,
        ItemShipmentStatus status,
        LocalDateTime orderDate
) {
}
