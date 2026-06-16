package com.example.cartservice.service.cart.query;

import com.example.cartservice.dao.cart.CartRepository;
import com.example.cartservice.dto.cart.CartResponse;
import com.example.cartservice.entity.cart.Cart;
import com.example.cartservice.service.cart.builder.CartResponseBuilder;
import com.example.cartservice.service.cart.cache.CartCacheService;
import com.example.cartservice.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartQueryService {

    private final SecurityService securityService;
    private final CartRepository cartRepository;
    private final CartCacheService cartCacheService;
    private final CartResponseBuilder cartResponseBuilder;

    public CartResponse getCurrentUserCart() {

        Long userId = securityService.getCurrentUserId();

        CartResponse cached = cartCacheService.get(userId);

        if (cached != null) {
            return cached;
        }

        Cart cart = cartRepository.findCartByUserId(userId)
                .orElse(new Cart());

        if (cart.getId() == null) {
            return new CartResponse();
        }

        CartResponse response = cartResponseBuilder.build(cart);

        cartCacheService.save(userId, response);

        return response;
    }

}
