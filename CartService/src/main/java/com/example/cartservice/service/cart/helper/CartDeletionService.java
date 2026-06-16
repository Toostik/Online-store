package com.example.cartservice.service.cart.helper;

import com.example.cartservice.dao.cart.CartRepository;
import com.example.cartservice.entity.cart.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartDeletionService {

    private final CartRepository cartRepository;

    public void delete(Cart cart) {
        cartRepository.delete(cart);
    }

}