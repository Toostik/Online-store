package com.example.orderservice.dto.order;

import com.example.orderservice.entity.enums.ItemShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private ItemShipmentStatus status;
}
