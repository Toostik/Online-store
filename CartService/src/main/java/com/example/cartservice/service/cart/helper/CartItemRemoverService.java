package com.example.cartservice.service.cart.helper;

import com.example.cartservice.dao.cart.CartItemsRepository;
import com.example.cartservice.entity.cart.Cart;
import com.example.cartservice.entity.cart.CartItem;
import com.example.cartservice.exceptions.cart.CartHasNotProductsException;
import com.example.cartservice.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemRemoverService {

    private final CartItemsRepository cartItemsRepository;
    private final SecurityService securityService;

    public Long remove(Long itemId) {

        Long userId = securityService.getCurrentUserId();

        CartItem item =
                cartItemsRepository.findByIdAndCartUserId(
                                itemId,
                                userId
                        )
                        .orElseThrow(
                                () ->
                                        new CartHasNotProductsException(
                                                "Cart item not found"
                                        )
                        );

        Cart cart = item.getCart();

        cart.getItems().remove(item);

        cartItemsRepository.delete(item);

        return userId;
    }

}