package com.example.cartservice.service.cart;

import com.example.cartservice.dto.cart.CartItemDto;
import com.example.cartservice.dto.cart.CartResponse;
import com.example.cartservice.service.cart.command.CartCommandService;
import com.example.cartservice.service.cart.query.CartQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartCommandService cartCommandService;
    private final CartQueryService cartQueryService;

    public CartResponse getCurrentUserCart() {
        return cartQueryService.getCurrentUserCart();
    }

    public void createCart(List<CartItemDto> items) {
        cartCommandService.createCart(items);
    }

    public void addItems(List<CartItemDto> items) {
        cartCommandService.addItems(items);
    }

    public void clearCart() {
        cartCommandService.clearCart();
    }

    public void deleteCart() {
        cartCommandService.deleteCart();
    }

    public void deleteItem(Long itemId) {
        cartCommandService.deleteItem(itemId);
    }

    public void updateQuantity(Long id, Integer quantity) {

        cartCommandService.updateQuantity(id, quantity);

    }

}