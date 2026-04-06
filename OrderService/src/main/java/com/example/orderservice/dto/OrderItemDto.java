package com.example.orderservice.dto;

import com.example.orderservice.entity.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long id;
    private Long product_id;
    private Integer quantity;
    private Integer priceAtPurchase;
}
