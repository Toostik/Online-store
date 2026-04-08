package com.example.orderservice.entity;

import com.example.orderservice.dto.OrderDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    @Column(name = "user_id")
    private Long userId;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private Status status;
    @Column(name = "total_amount")
    private Long totalAmount;
    @Column(name = "created_at")
    private LocalDate createdAt;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private OrderItem item;

    @ManyToOne
    private Order order;

    @OneToMany(mappedBy = "order")
    private List<Order> orders;

    public OrderDto toDto() {
        return new OrderDto(
                id,
                status != null ? status.name() : null,
                totalAmount,
                createdAt,
                item != null ? item.toDto() : null,
                orders != null ? orders.stream().map(Order::toDto).toList() : null
        );
    }
}
