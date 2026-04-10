package com.example.orderservice.service;

import com.example.orderservice.dao.OrderItemRepository;
import com.example.orderservice.dao.OrderRepository;
import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.dto.OrderItemDto;
import com.example.orderservice.dto.PaymentDto;
import com.example.orderservice.dto.request.OrderItemRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.entity.Status;
import com.example.orderservice.kafka.KafkaJsonProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final KafkaJsonProducer kafkaJsonProducer;

    @Autowired
    @Qualifier("userServiceWebClient")
    private final WebClient userServiceWebClient;

    @Autowired
    @Qualifier("productServiceWebClient")
    private final WebClient productServiceWebClient;


    public Boolean isProductsExists(List<Long> ids) {
        Boolean isProductsExists = productServiceWebClient.post()
                .uri("/api/products/exists")
                .bodyValue(ids)
                .retrieve()
                .toBodilessEntity()
                .map(response -> true)
                .onErrorReturn(false)
                .block();

        return isProductsExists;
    }

    public List<OrderDto> getAllOrders(Long id) {

        Boolean isUserExist = userServiceWebClient.get()
                .uri("/api/users/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .map(response -> true)
                .onErrorReturn(false)
                .block();

        List<Order> orders;
        if (isUserExist) {
            orders = orderRepository.findAllByUserId(id);
        } else {
            throw new RuntimeException("User doesn't exist");
        }

        if (orders.isEmpty()) {
            throw new RuntimeException("User doesn't have orders");
        }

        List<OrderDto> orderDtoList = orders.stream()
                .map(Order::toDto)
                .toList();

        return orderDtoList;
    }

    public OrderDto createOrder(String id, List<OrderItemRequest> items) {

        if (items.isEmpty()) {
            return new OrderDto();
        }

        List<Long> ids = items.stream().map(
                OrderItemRequest::getProductId
        ).toList();


        Map<Long, BigDecimal> prices = productServiceWebClient.post()
                .uri("/api/products/prices")
                .bodyValue(ids)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Long, BigDecimal>>() {
                }).block();

//        Из полученного списка товаров создаём список List<OrderItems>, сохраняем заказ
//        и помещаем в заказ товары
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setUserId(Long.valueOf(id));
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal price;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : items) {
            OrderItem item = new OrderItem();

            price = prices.get(itemRequest.getProductId());
            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(itemRequest.getQuantity())));

            // OrderItem
            item.setQuantity(itemRequest.getQuantity());
            item.setProductId(itemRequest.getProductId());
            item.setPriceAtPurchase(price);
            item.setOrder(order);

            orderItems.add(item);

        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        kafkaJsonProducer.sendMessage("orders-created", order.toDto());

        return order.toDto();
    }

    public void updateStatus(Status status, PaymentDto paymentDto) {
        Order order = orderRepository.findById(paymentDto.getOrderId()).orElse(null);
        assert order != null;
        order.setStatus(status);
        orderRepository.save(order);
        kafkaJsonProducer.sendMessage("orders-confirmed", order.toDto());
    }

}
