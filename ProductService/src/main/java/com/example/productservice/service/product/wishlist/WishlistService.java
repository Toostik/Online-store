package com.example.productservice.service.product.wishlist;

import com.example.productservice.dao.wishlist.WishlistRepository;
import com.example.productservice.entity.product.WishlistItem;
import com.example.productservice.service.security.SecurityService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final SecurityService securityService;
    private final WishlistRepository wishlistRepository;

    @Transactional
    public void putInWishlist(Long productId) {

        log.info("User start to add item to Wishlist -> {}", productId);

        Long userId = securityService.getCurrentUserId();

        WishlistItem item = new WishlistItem();

        item.setProductId(productId);
        item.setUserId(userId);

        wishlistRepository.save(item);

        log.info("User added item to Wishlist -> {}", productId);
    }
}
