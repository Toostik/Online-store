package com.example.cartservice.controller.query;

import com.example.cartservice.dto.cart.CartResponse;
import com.example.cartservice.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartQueryController {

    private final CartService cartService;

    @GetMapping("/my")
    public ResponseEntity<CartResponse> getCurrentUserCart() {

        return ResponseEntity.ok(
                cartService.getCurrentUserCart()
        );

    }

}