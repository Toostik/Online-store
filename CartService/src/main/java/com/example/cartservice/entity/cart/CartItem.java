package com.example.cartservice.entity.cart;

import com.example.cartservice.dto.cart.CartItemDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "cart")
@Table(
        name = "cart_items",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_cart_item_cart_product",
                columnNames = {"cart_id", "product_id"}
        )
)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "price_at_add_time")
    private BigDecimal priceAtAddTime;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public CartItem(Integer quantity, Long productId, BigDecimal priceAtAddTime, Cart cart) {
        this.quantity = quantity;
        this.productId = productId;
        this.priceAtAddTime = priceAtAddTime;
        this.cart = cart;
    }

    public CartItemDto toDto() {
        return new CartItemDto(
          quantity,
          productId
        );
    }
}
