package com.example.productservice.controller;

import com.example.productservice.dto.ProductDto;
import com.example.productservice.dto.request.CreateProductRequest;
import com.example.productservice.dto.request.UpdateProductRequest;
import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/all")
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

    @GetMapping("/{id}/price")
    public BigDecimal getPriceById(@PathVariable Long id){
        return productService.getPriceById(id);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,@RequestBody UpdateProductRequest request){
        productService.updateProduct(id,request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity<ProductDto> createProduct(@RequestBody CreateProductRequest request){
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

}
