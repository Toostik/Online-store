package com.example.cartservice.service.cart.helper;

import com.example.cartservice.dao.cart.CartItemsRepository;
import com.example.cartservice.entity.cart.CartItem;
import com.example.cartservice.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemUpdaterService {

    private final CartItemsRepository cartItemsRepository;
    private final SecurityService securityService;

    public Long updateQuantity(Long id, Integer quantity) {

        Long userId = securityService.getCurrentUserId();

        CartItem item = cartItemsRepository.findById(id)
                .orElseThrow();

        if (!item.getCart().getUserId().equals(userId)) {
            throw new AccessDeniedException("Item does not belong to user");
        }

        item.setQuantity(quantity);

        return userId;

    }

}
