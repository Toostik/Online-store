package com.example.orderservice.entity;

import com.example.orderservice.dto.OrderItemDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "product_id")
    private Long product_id;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "price_at_purchase")
    private Integer priceAtPurchase;

    @OneToOne(fetch = FetchType.LAZY)
    private Order order;

    public OrderItemDto toDto(){
        return new OrderItemDto(
                id,
                product_id,
                quantity,
                priceAtPurchase
        );
    }

}
