package com.example.cartservice.feign;

import com.example.cartservice.dto.product.ProductDto;
import com.example.cartservice.dto.product.ProfileProducts;
import com.example.cartservice.dto.product.request.CheckProductRequest;
import com.example.cartservice.dto.product.request.CheckProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(name = "product-service", url = "${product.service.url}")
public interface ProductClient {

    @GetMapping("/api/v1/products/{id}/price")
    BigDecimal getPrice(@PathVariable Long id);

    @GetMapping("/api/v1/products/{id}")
    ProductDto getProduct(@PathVariable Long id);

    @PostMapping("/api/v1/products/availability")
    CheckProductResponse getProductsAvailability(@RequestBody CheckProductRequest request);

    @PostMapping("/api/v1/products/prices")
    Map<Long, BigDecimal> getPrices(@RequestBody List<Long> ids);

    @PostMapping("/api/v1/products/query")
    ProfileProducts getProductsByIds(@RequestBody List<Long> ids);
}
