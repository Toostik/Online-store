package com.example.userservice.feign;

import com.example.userservice.dto.orders.ProfileOrders;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "order-service", url = "http://order-service:8085")
public interface OrderClient {

    @PostMapping("/api/orders/my/items")
    ProfileOrders getRecentOrders();

    }
