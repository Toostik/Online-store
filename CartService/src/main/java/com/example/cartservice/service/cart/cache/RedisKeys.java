package com.example.cartservice.service.cart.cache;

public final class RedisKeys {

    private RedisKeys() {
    }

    public static String cart(Long userId) {
        return "cart:" + userId;
    }

}