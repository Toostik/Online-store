package com.example.cartservice.entity;

import com.example.cartservice.dto.CartItemDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "cart")
@Table(name = "cart_items")
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
          id,
          quantity,
          productId
        );
    }
}
