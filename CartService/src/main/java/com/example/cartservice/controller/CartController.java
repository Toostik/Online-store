package com.example.cartservice.controller;

import com.example.cartservice.dto.CartDto;
import com.example.cartservice.dto.CartItemDto;
import com.example.cartservice.service.CartQueryService;
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
    private final CartQueryService cartQueryService;

    @GetMapping("/my")
    public ResponseEntity<CartDto> getCartByCurrentUser(){
        return ResponseEntity.ok(cartQueryService.getCartByCurrentUser());
    }

    @PostMapping("/create/cart")
    public ResponseEntity<?> createCart(@RequestBody List<CartItemDto> items){
        cartService.createCart(items);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody List<CartItemDto> items){
        cartService.addToCart(items);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/create/order")
    public ResponseEntity<?> createOrderByCurrentUser(){
        cartService.createOrder();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/cart")
    public void deleteCart(){
        cartService.deleteCart();
    }

    @DeleteMapping("/delete/item/{id}")
    public void deleteCartItem(@PathVariable Long id){
        cartService.deleteCartItem(id);
    }



}
