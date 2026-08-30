package com.example.productservice.controller.wishlist;

import com.example.productservice.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/wishlist")
public class WishlistController {

    private final ProductService productService;

    @PostMapping("/{id}")
    public ResponseEntity<?> putItemInWishList(@PathVariable(name = "id") Long productId){
        productService.putInWishlist(productId);
        return ResponseEntity.ok().build();
    }

}
