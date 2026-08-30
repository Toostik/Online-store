package com.example.orderservice.feign;

import com.example.orderservice.dto.cart.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "cart-service", url = "${services.cart.url}")
public interface CartClient {
    @GetMapping("/api/v1/carts/my")
    CartResponse getCart();
}
