package com.example.cartservice.controller.command;

import com.example.cartservice.dto.cart.CartItemDto;
import com.example.cartservice.service.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartCommandController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<Void> createCart(
            @RequestBody(required = false)
            List<CartItemDto> items
    ) {

        cartService.createCart(items);

        return ResponseEntity.ok().build();

    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCart() {

        cartService.deleteCart();

        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long id
    ) {

        cartService.deleteItem(id);

        return ResponseEntity.ok().build();

    }

    @PutMapping("/items/{id}")
    public ResponseEntity<Void> updateQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity
    ) {
        cartService.updateQuantity(id, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/items")
    public ResponseEntity<Void> addItems(
            @Valid
            @RequestBody
            List<CartItemDto> items
    ) {

        cartService.addItems(items);

        return ResponseEntity.ok().build();

    }

}