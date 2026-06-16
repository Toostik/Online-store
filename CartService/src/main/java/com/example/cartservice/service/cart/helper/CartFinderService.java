package com.example.cartservice.service.cart.helper;

import com.example.cartservice.dao.cart.CartRepository;
import com.example.cartservice.entity.cart.Cart;
import com.example.cartservice.exceptions.cart.CartNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartFinderService {

    private final CartRepository cartRepository;

    public Cart findByUserId(Long userId) {

        return cartRepository.findCartByUserId(userId)
                .orElseThrow(
                        () -> new CartNotFoundException(userId)
                );
    }

}