package com.example.orderservice.feign;

import com.example.orderservice.dto.product.ProfileProducts;
import com.example.orderservice.dto.product.request.CheckProductRequest;
import com.example.orderservice.dto.product.request.CheckProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(name = "product-service", url = "${services.product.url}")
public interface ProductClient {
    @GetMapping("/api/v1/products/{id}/price")
    BigDecimal gerPrice(@PathVariable Long id);
    @PostMapping("/api/v1/products/prices")
    Map<Long, BigDecimal> getPrices(@RequestBody List<Long> ids);
    @PostMapping("/api/v1/products/query")
    ProfileProducts getProductsByIds(@RequestBody List<Long> ids);

    @PostMapping("/internal/products/availability")
    CheckProductResponse getProductsAvailability(@RequestBody CheckProductRequest request);


}
