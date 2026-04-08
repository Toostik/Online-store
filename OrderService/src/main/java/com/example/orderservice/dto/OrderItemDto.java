package com.example.orderservice.dto;

import com.example.orderservice.entity.ItemShipmentStatus;
import com.example.orderservice.entity.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long id;
    private Long product_id;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private ItemShipmentStatus status;
}
