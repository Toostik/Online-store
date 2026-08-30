package com.example.orderservice.service.order.command;

import com.example.orderservice.dao.order.OrderRepository;
import com.example.orderservice.dto.order.OrderContext;
import com.example.orderservice.dto.order.OrderDto;
import com.example.orderservice.dto.order.request.CreateOrderRequest;
import com.example.orderservice.entity.order.Order;
import com.example.orderservice.entity.order.mapper.OrderMapper;
import com.example.orderservice.service.delivery.DeliveryService;
import com.example.orderservice.service.order.builder.OrderBuilderService;
import com.example.orderservice.service.order.cache.OrderCacheService;
import com.example.orderservice.service.order.event.OrderOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.flashsale.FlashSaleReservationAndCheckoutEvent;
import org.example.events.order.OrderCreatedEvent;
import org.example.events.order.OrderItemEvent;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderTransactionalService {

    private final OrderRepository orderRepository;
    private final OrderBuilderService builderService;
    private final DeliveryService deliveryService;
    private final OrderOutboxService outboxService;
    private final OrderCacheService cacheService;
    private final OrderMapper orderMapper;

    public OrderDto createOrder(
            OrderContext context,
            CreateOrderRequest request,
            Long userId
    ) {

        Order order =
                builderService.build(
                        context.cart(),
                        context.prices(),
                        userId
                );

        deliveryService.attachDelivery(
                order,
                request,
                userId
        );

        Order saved =
                orderRepository.save(order);

        publishAfterCommit(saved, null);

        return orderMapper.toDto(saved);
    }

    public void createFlashSaleOrder(
            FlashSaleReservationAndCheckoutEvent event
    ) {

        Order order =
                builderService.buildFlashSaleOrder(
                        event,
                        event.discountedPrice()
                );

        Order saved =
                orderRepository.save(order);

        publishAfterCommit(
                saved,
                event.reservationKey()
        );

    }

    private void publishAfterCommit(
            Order order,
            UUID reservationKey
    ) {

        List<OrderItemEvent> items =
                order.getItems()
                        .stream()
                        .map(item ->
                                new OrderItemEvent(
                                        item.getProductId(),
                                        item.getQuantity()
                                ))
                        .toList();

        OrderCreatedEvent event =
                new OrderCreatedEvent(
                        UUID.randomUUID().toString(),
                        MDC.get("requestId"),
                        order.getId(),
                        order.getUserId(),
                        order.getTotalAmount(),
                        items,
                        reservationKey
                );

        outboxService.publishCreated(event);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {

                        try {
                            cacheService.save(order);
                        } catch (Exception ex) {
                            log.warn("Redis unavailable", ex);
                        }

                    }

                });
    }

}
