package com.example.orderservice.controller;

import com.example.orderservice.dto.CartDto;
import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.dto.request.OrderItemRequest;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders(@AuthenticationPrincipal Jwt jwt){
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(orderService.getAllOrders(userId));
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody CartDto cart){
        return ResponseEntity.ok(orderService.createOrder(cart));
    }
}
