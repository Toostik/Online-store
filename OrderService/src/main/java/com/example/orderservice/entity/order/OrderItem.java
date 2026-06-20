package com.example.orderservice.entity.order;

import com.example.orderservice.dto.order.OrderItemDto;
import com.example.orderservice.entity.enums.ItemShipmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
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
