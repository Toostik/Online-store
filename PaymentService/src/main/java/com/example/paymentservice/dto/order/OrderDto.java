package com.example.paymentservice.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private String eventId;

    private List<OrderItemDto> items;

    public OrderDto(Long id,
                    Long userId,
                    String status,
                    BigDecimal totalAmount,
                    LocalDateTime createdAt,
                    List<OrderItemDto> items) {

        this.id = id;
        this.userId = userId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.items = items;
    }

}

