package com.example.productservice.controller.product.command;

import com.example.productservice.dto.product.ProductDto;
import com.example.productservice.dto.product.request.CreateProductRequest;
import com.example.productservice.dto.product.request.UpdateProductRequest;
import com.example.productservice.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductCommandController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest request){
        log.info("POST /api/products - creating product: name={}, categoryId={}",
                request.getName(), request.getCategoryId());
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        log.info("DELETE /api/products/{} - deleting product", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,@RequestBody UpdateProductRequest request){
        log.info("PUT /api/products/update/{} - updating product", id);
        productService.updateProduct(id,request);
        return ResponseEntity.ok().build();
    }

    //MiniO
    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadFile(@RequestParam("images") MultipartFile[] file, @PathVariable Long id){
        productService.uploadImages(file, id);
        return ResponseEntity.ok().build();
    }

}
