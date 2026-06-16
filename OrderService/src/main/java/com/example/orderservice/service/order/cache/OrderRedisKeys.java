package com.example.orderservice.service.order.cache;

public final class OrderRedisKeys {

    private OrderRedisKeys(){}

    public static String order(Long id){
        return "order:" + id;
    }

    public static String userOrders(Long userId){
        return "user:" + userId + ":orders";
    }

}
