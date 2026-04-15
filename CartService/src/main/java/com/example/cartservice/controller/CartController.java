package com.example.cartservice.controller;

import com.example.cartservice.dto.CartDto;
import com.example.cartservice.dto.CartItemDto;
import com.example.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;
    @GetMapping
    public ResponseEntity<List<CartItemDto>> getCart(@RequestHeader("X-User-Id") Long id){
        return ResponseEntity.ok(cartService.getCart(id));
    }
    @PostMapping
    public ResponseEntity<?> createCart(@AuthenticationPrincipal Jwt jwt,
                                        @RequestBody List<CartItemDto> items){
        Long userId = Long.valueOf(jwt.getSubject());
        cartService.createCart(userId, items);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@AuthenticationPrincipal Jwt jwt,
                                        @RequestBody List<CartItemDto> items){
        Long userId = Long.valueOf(jwt.getSubject());
        cartService.addToCart(userId, items);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/create-order")
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal Jwt jwt){
        Long userId = Long.valueOf(jwt.getSubject());
        cartService.createOrder(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/delete")
    public void deleteCart(){
        cartService.deleteCart();
    }

    @DeleteMapping("/remove/delete/item/{id}")
    public void deleteCartItem(@PathVariable Long id){
        cartService.deleteCartItem(id);
    }



}
