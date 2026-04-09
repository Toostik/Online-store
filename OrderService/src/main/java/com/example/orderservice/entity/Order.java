package com.example.orderservice.entity;

import com.example.orderservice.dto.OrderDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.CREATED;
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true)

    private List<OrderItem> items;

    public OrderDto toDto() {
        return new OrderDto(
                id,
                userId,
                status != null ? status.name() : null,
                totalAmount,
                createdAt,
                items.stream().map(OrderItem::toDto).toList()
        );
    }
}
