package com.example.orderservice.dto;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.entity.Status;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor

public class OrderDto {
    private Long id;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    private List<OrderItemDto> items;

    @JsonCreator
    public OrderDto(@JsonProperty("id") Long id,
                    @JsonProperty("status") String status,
                    @JsonProperty("total_amount") BigDecimal totalAmount,
                    @JsonProperty("created_at") LocalDateTime createdAt,
                    @JsonProperty("items") List<OrderItemDto> items) {

        this.id = id;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.items = items;
    }

}
