package com.example.orderservice.service.integration;

import com.example.orderservice.dto.cart.CartResponse;
import com.example.orderservice.exceptions.cart.CartEmptyException;
import com.example.orderservice.exceptions.cart.CartServiceException;
import com.example.orderservice.feign.CartClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {

    private final CartClient cartClient;

    public CartResponse getCart() {
        return cartClient.getCart();
    }

    public CartResponse getValidatedCart() {

        CartResponse cart = cartClient.getCart();

        if (cart == null ||
                cart.getItems() == null ||
                cart.getItems().isEmpty()) {

            throw new CartEmptyException();
        }

        return cart;
    }


}
