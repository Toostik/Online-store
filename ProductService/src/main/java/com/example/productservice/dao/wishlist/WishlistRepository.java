package com.example.productservice.dao.wishlist;

import com.example.productservice.entity.product.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {

    Integer countByUserId(Long userId);

}
