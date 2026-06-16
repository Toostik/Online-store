package com.example.productservice.exceptions.product;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId) {
        super("Product not found " + productId);
    }
}
