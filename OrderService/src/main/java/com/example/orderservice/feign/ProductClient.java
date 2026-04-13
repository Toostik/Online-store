package com.example.orderservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name = "product-service", url = "http://product-service:8083")
public interface ProductClient {
    @GetMapping("/api/products/{id}/price")
    BigDecimal gerPrice(@PathVariable Long id);

}
