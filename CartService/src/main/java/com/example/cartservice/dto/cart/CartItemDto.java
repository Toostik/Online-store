package com.example.cartservice.dto.cart;

import com.example.cartservice.entity.cart.Cart;
import com.example.cartservice.entity.cart.CartItem;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {

    @NotNull(message = "Quantity must be filled in")
    private Integer quantity;
    @NotNull(message = "Product id must be filled in")
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
