package com.example.cartservice.feign;

import com.example.cartservice.dto.request.CheckProductRequest;
import com.example.cartservice.dto.request.CheckProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(name = "product-service", url = "http://product-service:8083")
public interface ProductClient {
    @GetMapping("/api/products/{id}/price")
    BigDecimal getPrice(@PathVariable Long id);

    @PostMapping("/api/products/check-availability")
    CheckProductResponse getProductsAvailability(@RequestBody CheckProductRequest request);

}
