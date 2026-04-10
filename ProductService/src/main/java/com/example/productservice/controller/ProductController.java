package com.example.productservice.controller;

import com.example.productservice.dto.ProductDto;
import com.example.productservice.service.CategoryService;
import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long productId){
        return ResponseEntity.ok(productService.getProductById(productId));
    }
    @PostMapping("/prices")
    public Map<Long, BigDecimal> getPrices(@RequestBody List<Long> ids){
        return productService.getPrices(ids);
    }

    @PostMapping("/exists")
    public Boolean isProductsExists(@RequestBody List<Long> ids){
        return productService.isProductExists(ids);
    }
}
