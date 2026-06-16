package com.example.orderservice.feign;

import com.example.orderservice.dto.cart.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "cart-service", url = "http://cart-service:8084")
public interface CartClient {
    @GetMapping("/api/carts/my")
    CartResponse getCart();
}
