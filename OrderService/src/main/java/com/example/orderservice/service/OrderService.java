package com.example.orderservice.service;

import com.example.orderservice.dao.OrderItemRepository;
import com.example.orderservice.dao.OrderRepository;
import com.example.orderservice.dto.OrderDto;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public OrderDto createOrder(String id, List<OrderItemRequest> items) {

        List<Long> ids = items.stream().map(
                OrderItemRequest::getProductId
        ).toList();

        Boolean isProductsExists = productServiceWebClient.post()
                .uri("/api/products/exists")
                .bodyValue(ids)
                .retrieve()
                .toBodilessEntity()
                .map(response -> true)
                .onErrorReturn(false)
                .block();

        Map<Long, Long> prices = productServiceWebClient.post()
                .uri("/api/products/prices")
                .bodyValue(ids)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Long, Long>>() {
                }).block();

        Map<Long, Integer> quantityOfProducts = new HashMap<>();
//        Создание переменной количества продуктов для отправки в kafka.
        if (Boolean.TRUE.equals(isProductsExists)) {
            for (OrderItemRequest item : items) {
                quantityOfProducts.put(item.getProductId(), item.getQuantity());
            }
        } else {
            throw new RuntimeException("Product doesn't exist");
        }

        if (items.isEmpty()) {
            return new OrderDto();
        }

        List<Order> orders = new ArrayList<>();
        List<OrderItem> orderItems = new ArrayList<>();

//        Из полученного списка из запроса создаём OrderItems и к каждопу присваиваем заказ
//        затем сохраняем в базу

        for (OrderItemRequest itemRequest : items) {

            Order order = new Order();
            OrderItem orderItem = new OrderItem();

            Long price = prices.get(itemRequest.getProductId());
            Long totalAmount = price * itemRequest.getQuantity();

            // Order
            order.setCreatedAt(LocalDate.now());
            order.setStatus(Status.CREATED);
            order.setUserId(Long.valueOf(id));
            order.setTotalAmount(totalAmount);

            // СНАЧАЛА сохраняем Order
            orderRepository.save(order);

            // OrderItem
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setProduct_id(itemRequest.getProductId());
            orderItem.setPriceAtPurchase(Math.toIntExact(price));
            orderItem.setOrder(order);

            orderItems.add(orderItem);
            orders.add(order);
        }

// Если заказов нет
        if (orders.isEmpty()) {
            return new OrderDto();
        }

//Создаём parentOrder
        Order parentOrder = new Order();

        Long totalAmount = 0L;
        boolean hasCancelled = false;

// считаем сумму и статус
        for (Order order : orders) {
            totalAmount += order.getTotalAmount();

            if (order.getStatus() == Status.CANCELLED) {
                hasCancelled = true;
            }
        }

// статус родителя
        if (hasCancelled) {
            parentOrder.setStatus(Status.PARTLY);
        } else {
            parentOrder.setStatus(Status.CREATED);
        }

        parentOrder.setOrders(orders);
        parentOrder.setTotalAmount(totalAmount);
        parentOrder.setUserId(Long.valueOf(id));
        parentOrder.setCreatedAt(LocalDate.now());

// Сохраняем parent
        orderRepository.save(parentOrder);

// Kafka
        kafkaJsonProducer.sendMessage(parentOrder.toDto(), quantityOfProducts);

        return parentOrder.toDto();
    }
}
