package com.example.orderservice.service.order.query;

import com.example.orderservice.dao.order.OrderRepository;
import com.example.orderservice.dto.order.OrderDto;
import com.example.orderservice.dto.order.request.OrderPaymentInfoResponse;
import com.example.orderservice.entity.order.Order;
import com.example.orderservice.entity.order.mapper.OrderMapper;
import com.example.orderservice.exceptions.order.OrderNotFoundException;
import com.example.orderservice.service.order.cache.OrderCacheService;
import com.example.orderservice.service.security.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final SecurityService securityService;
    private final OrderRepository orderRepository;
    private final OrderCacheService cacheService;
    private final OrderMapper orderMapper;

    public OrderDto getOrderById(Long id){

        OrderDto cached =
                cacheService.getOrder(id);

        if(cached != null){
            return cached;
        }

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(
                                () -> new OrderNotFoundException(id)
                        );

        try {

            cacheService.save(order);

        }
        catch (Exception ex) {

            log.warn("Cache exception getting order");

        }

        return orderMapper.toDto(order);

    }

    public List<OrderDto> getAllOrdersOfCurrentUser(){

        Long userId =
                securityService.getCurrentUserId();

        List<Order> orders =
                orderRepository.findAllByUserId(userId);

        return orders.stream()
                .map(orderMapper::toDto)
                .toList();
    }


}
