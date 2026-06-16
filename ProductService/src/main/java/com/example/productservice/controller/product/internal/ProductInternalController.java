package com.example.productservice.controller.product.internal;

import com.example.productservice.dto.product.request.CheckProductRequest;
import com.example.productservice.dto.product.request.CheckProductResponse;
import com.example.productservice.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/internal/products")
@Slf4j
@RestController
public class ProductInternalController {

    private final ProductService productService;

    @PostMapping("/availability")
    public ResponseEntity<CheckProductResponse> getProductAvailability(@RequestBody CheckProductRequest request){
        return ResponseEntity.ok(productService.getAvailability(request));
    }

    @PostMapping("/exists")
    public ResponseEntity<Boolean> isProductsExists(@RequestBody List<Long> ids){
        return ResponseEntity.ok(productService.isProductExists(ids));
    }

}
