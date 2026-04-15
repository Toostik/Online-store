package com.example.orderservice.service;

import com.example.orderservice.dao.OrderItemRepository;
import com.example.orderservice.dao.OrderRepository;
import com.example.orderservice.dto.*;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.entity.Status;
import com.example.orderservice.kafka.KafkaJsonProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final KafkaJsonProducer kafkaJsonProducer;
    private final PriceService priceService;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public List<OrderDto> getAllOrdersOfCurrentUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());

        String userOrdersKey = "user:" + userId + ":orders";

        List<Object> orderIds = redisTemplate.opsForList()
                .range(userOrdersKey, 0, -1);

        if (orderIds == null || orderIds.isEmpty()) {

            List<Order> ordersFromDb = orderRepository.findAllByUserId(userId);

            if (ordersFromDb.isEmpty()) {
                return List.of();
            }

            List<OrderDto> dtos = ordersFromDb.stream()
                    .map(Order::toDto)
                    .toList();

            for (OrderDto order : dtos) {
                String orderKey = "order:" + order.getId();
                redisTemplate.opsForValue().set(orderKey, order, Duration.ofHours(1));

                redisTemplate.opsForList().rightPush(userOrdersKey, order.getId());
            }

            return dtos;
        }

        List<String> keys = orderIds.stream()
                .map(id -> "order:" + id.toString())
                .toList();

        List<Object> cachedOrders = redisTemplate.opsForValue().multiGet(keys);

        if (cachedOrders == null) {
            return List.of();
        }

        return cachedOrders.stream()
                .filter(Objects::nonNull)
                .map(o -> objectMapper.convertValue(o, OrderDto.class))
                .toList();
    }

    public OrderDto createOrder(CartDto cart) {
        List<CartItemDto> items = cart.getItems();

        if (items.isEmpty()) {
            return new OrderDto();
        }

        List<Long> ids = items.stream().map(
                CartItemDto::getProductId
        ).toList();


        Map<Long, BigDecimal> prices = priceService.getPrices(ids);

//        Из полученного списка товаров создаём список List<OrderItems>, сохраняем заказ
//        и помещаем в заказ товары

        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setUserId(cart.getUserId());
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal price;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItemDto item : items) {
            OrderItem orderItem = new OrderItem();

            price = prices.get(item.getProductId());
            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));

            // OrderItem
            orderItem.setQuantity(item.getQuantity());
            orderItem.setProductId(item.getProductId());
            orderItem.setPriceAtPurchase(price);
            orderItem.setOrder(order);

            orderItems.add(orderItem);

        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        kafkaJsonProducer.sendMessage("orders-created", order.toDto());

        log.info("Order created -> {}", order.getId());

        return order.toDto();
    }

    public void updateStatus(Status status, PaymentDto paymentDto) {
        Order order = orderRepository.findById(paymentDto.getOrderId()).orElse(null);
        assert order != null;
        order.setStatus(status);
        orderRepository.save(order);
        kafkaJsonProducer.sendMessage("orders-confirmed", order.toDto());
        log.info("Order confirmed -> {}", order.getId());
    }

    public OrderDto getOrderById(Long id) {

        String key = "order:" + id;

        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return objectMapper.convertValue(cached, OrderDto.class);
        }

        Order orderFromDb = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found!"));

        OrderDto dto = orderFromDb.toDto();

        redisTemplate.opsForValue().set(key, dto, Duration.ofHours(1));

        return dto;
    }
}
