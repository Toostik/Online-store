package com.example.cartservice.dto;

import com.example.cartservice.entity.Cart;
import com.example.cartservice.entity.CartItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {

    private Long id;
    private Integer quantity;
    private Long productId;

    public CartItem toEntity(Cart cart, BigDecimal price){
        return new CartItem(
                quantity,
                productId,
                price,
                cart
        );
    }
}
