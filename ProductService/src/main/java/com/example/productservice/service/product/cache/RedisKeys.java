package com.example.productservice.service.product.cache;

public final class RedisKeys {

    private RedisKeys(){}

    public static String product(Long id){
        return "product:" + id;
    }

    public static String productPrice(Long id){
        return "product:price:" + id;
    }

}
