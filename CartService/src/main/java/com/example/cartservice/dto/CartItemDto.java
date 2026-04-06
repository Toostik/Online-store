package com.example.cartservice.dto;

import com.example.cartservice.entity.Cart;
import com.example.cartservice.entity.CartItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {

    private Long id;
    private Long quantity;
    private Long productId;

    public CartItemDto(Long quantity, Long productId) {
        this.quantity = quantity;
        this.productId = productId;
    }

    public CartItem toEntity(Cart cart, Long price){
        return new CartItem(
                quantity,
                productId,
                price,
                cart
        );
    }
}
