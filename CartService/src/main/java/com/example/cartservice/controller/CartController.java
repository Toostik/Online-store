package com.example.cartservice.controller;

import com.example.cartservice.dto.CartDto;
import com.example.cartservice.dto.CartItemDto;
import com.example.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createCart(@RequestHeader("X-User-Id") Long id,
                                        @RequestBody List<CartItemDto> items){
        cartService.createCart(id, items);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestHeader("X-User-Id") Long id){
        cartService.createOrder(id);
        return ResponseEntity.ok().build();
    }

}
