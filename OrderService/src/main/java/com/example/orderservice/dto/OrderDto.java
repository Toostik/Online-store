package com.example.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor

public class OrderDto {
    private Long id;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    private List<OrderItemDto> items;

    @JsonCreator
    public OrderDto(@JsonProperty("id") Long id,
                    @JsonProperty("user_id") Long userId,
                    @JsonProperty("status") String status,
                    @JsonProperty("total_amount") BigDecimal totalAmount,
                    @JsonProperty("created_at") LocalDateTime createdAt,
                    @JsonProperty("items") List<OrderItemDto> items) {

        this.id = id;
        this.userId = userId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.items = items;
    }

}
