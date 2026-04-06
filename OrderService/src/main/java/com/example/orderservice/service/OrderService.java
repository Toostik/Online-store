package com.example.orderservice.service;

import com.example.orderservice.dao.OrderRepository;
import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.entity.Order;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    @Autowired
    @Qualifier("userServiceWebClient")
    private final WebClient userServiceWebClient;

    public List<OrderDto> getAllOrders(Long id) {

        Boolean isUserExist = userServiceWebClient.get()
                .uri("/api/users/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .map(response -> true)
                .onErrorReturn(false)
                .block();

        List<Order> orders;
        if(isUserExist){
            orders = orderRepository.findAllByUserId(id);
        }else{
            throw new RuntimeException("User doesn't exist");
        }

        if(orders.isEmpty()){
            throw new RuntimeException("User doesn't have orders");
        }

        List<OrderDto> orderDtoList = orders.stream()
                .map(Order::toDto)
                .toList();

        return orderDtoList;
    }
}
