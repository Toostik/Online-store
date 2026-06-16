package com.example.cartservice.service.integration;

public final class RedisKeys {

    private RedisKeys(){}

    public static String product(Long id){
        return "product:" + id;
    }

    public static String productPrice(Long id){
        return "product:price:" + id;
    }

}
