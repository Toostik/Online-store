package com.example.orderservice.entity;

import com.example.orderservice.dto.OrderItemDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @Column(name = "price_at_purchase")
    private BigDecimal priceAtPurchase;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ItemShipmentStatus status = ItemShipmentStatus.NOT_SHIPPED;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderItemDto toDto(){
        return new OrderItemDto(
                id,
                productId,
                quantity,
                priceAtPurchase,
                status
        );
    }

}
