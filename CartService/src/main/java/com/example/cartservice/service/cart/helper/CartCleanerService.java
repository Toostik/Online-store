package com.example.cartservice.service.cart.helper;

import com.example.cartservice.entity.cart.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartCleanerService {

    public void clear(Cart cart) {
        cart.getItems().clear();
    }

}