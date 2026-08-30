package com.example.userservice.feign;

import com.example.userservice.dto.orders.ProfileOrders;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "order-service", url = "${services.order.url}")
public interface OrderClient {

    @PostMapping("/api/v1/orders/my/items")
    ProfileOrders getRecentOrders();

    }
