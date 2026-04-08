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

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor

public class OrderDto {
    private Long id;
    private String status;
    private Long totalAmount;
    private LocalDate createdAt;

    private OrderItemDto items;
    private List<OrderDto> orders;

    @JsonCreator
    public OrderDto(@JsonProperty("id") Long id,
                    @JsonProperty("status") String status,
                    @JsonProperty("total_amount") Long total_amount,
                    @JsonProperty("created_at") LocalDate createdAt,
                    @JsonProperty("items") OrderItemDto items,
                    @JsonProperty("orders") List<OrderDto> orders) {

        this.id = id;
        this.status = status;
        this.totalAmount = total_amount;
        this.createdAt = createdAt;
        this.items = items;
        this.orders = orders;
    }

}
