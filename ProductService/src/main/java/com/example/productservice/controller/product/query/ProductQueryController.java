package com.example.productservice.controller.product.query;

import com.example.productservice.dto.product.ProductDto;
import com.example.productservice.dto.product.ProfileProducts;
import com.example.productservice.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductQueryController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getPageProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size){
        return ResponseEntity.ok(productService.getPageProducts(page,size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long productId){
        log.info("GET /api/products/{} - fetching product", productId);
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @PostMapping("/query")
    public ResponseEntity<ProfileProducts> getProductsByIds(@RequestBody List<Long> ids){
        log.info("GET Products by-ids");
        return ResponseEntity.ok(productService.getProductsByIds(ids));
    }

    @GetMapping("/preview")
    public ResponseEntity<List<ProductDto>> getProductsWithImages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size){
        return ResponseEntity.ok(productService.getProductsWithImages(page,size));
    }

    @PostMapping("/prices")
    public ResponseEntity<Map<Long, BigDecimal>> getPrices(@RequestBody List<Long> ids){
        return ResponseEntity.ok(productService.getPrices(ids));
    }

    @GetMapping("/{id}/price")
    public ResponseEntity<BigDecimal> getPriceById(@PathVariable Long id){
        return ResponseEntity.ok(productService.getPriceById(id));
    }

}
