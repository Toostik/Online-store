package com.example.orderservice.service.delivery;

import com.example.orderservice.dto.order.request.CreateOrderRequest;
import com.example.orderservice.entity.address.Address;
import com.example.orderservice.entity.delivery.Delivery;
import com.example.orderservice.entity.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    public void attachDelivery(
            Order order,
            CreateOrderRequest request,
            Long userId
    ){

        Address address = Address.builder()
                .userId(userId)
                .country(request.country())
                .city(request.city())
                .address(request.address())
                .apartment(request.apartment())
                .postalCode(request.postalCode())
                .build();

        Delivery delivery = Delivery.builder()
                .type(request.deliveryMethod())
                .price(new BigDecimal("495"))
                .estimatedDays(2)
                .build();

        order.setAddress(address);
        order.setDelivery(delivery);
    }

}
