package com.example.userservice.service.integration;

import com.example.userservice.dto.orders.ProfileOrders;
import com.example.userservice.feign.OrderClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderClient orderClient;

    public ProfileOrders getRecentProducts(){
        return orderClient.getRecentOrders();
    }
}
