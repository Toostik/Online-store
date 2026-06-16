package com.example.cartservice.service.cart.helper;

import com.example.cartservice.dao.cart.CartRepository;
import com.example.cartservice.entity.cart.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartCreatorService {

    private final CartRepository cartRepository;

    public Cart create(Long userId) {

        try {

            Cart cart = new Cart();

            cart.setUserId(userId);

            return cartRepository.save(cart);

        } catch (DataIntegrityViolationException ex) {

            return cartRepository.findCartByUserId(userId)
                    .orElseThrow(
                            () -> ex
                    );
        }

    }

}