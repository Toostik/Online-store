package com.example.userservice.dto.orders;


import com.example.userservice.entity.enums.ItemShipmentStatus;

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
