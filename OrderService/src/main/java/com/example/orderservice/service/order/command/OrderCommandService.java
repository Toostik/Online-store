package com.example.orderservice.service.order.command;

import com.example.orderservice.dao.event.ProcessedEventRepository;
import com.example.orderservice.dto.cart.CartItemResponse;
import com.example.orderservice.dao.order.OrderRepository;
import com.example.orderservice.dto.cart.CartResponse;
import com.example.orderservice.dto.order.OrderDto;
import com.example.orderservice.dto.order.request.CreateOrderRequest;
import com.example.orderservice.dto.product.ProductAvailability;
import com.example.orderservice.dto.product.request.CheckProductRequest;
import com.example.orderservice.dto.product.request.CheckProductResponse;
import com.example.orderservice.entity.enums.OrderStatus;
import com.example.orderservice.entity.event.ProcessedEvent;
import com.example.orderservice.entity.order.Order;
import com.example.orderservice.entity.order.mapper.OrderMapper;
import com.example.orderservice.exceptions.product.ProductNotFoundException;
import com.example.orderservice.exceptions.product.ProductOutOfStockException;
import com.example.orderservice.service.delivery.DeliveryService;
import com.example.orderservice.service.integration.CartService;
import com.example.orderservice.service.integration.ProductService;
import com.example.orderservice.service.order.builder.OrderBuilderService;
import com.example.orderservice.service.order.cache.OrderCacheService;
import com.example.orderservice.service.order.event.OrderOutboxService;
import com.example.orderservice.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.flashsale.FlashSaleReservationAndCheckoutEvent;
import org.example.events.order.*;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandService {

    private final SecurityService securityService;
    private final CartService cartService;
    private final ProductService productService;

    private final OrderBuilderService builderService;
    private final DeliveryService deliveryService;

    private final OrderRepository orderRepository;

    private final OrderOutboxService outboxService;
    private final OrderCacheService cacheService;
    private final OrderMapper orderMapper;
    private final ProcessedEventRepository processedEventRepository;

    private boolean markProcessed(String eventId) {

        try {

            processedEventRepository.save(
                    new ProcessedEvent(eventId)
            );

            return true;
        }
        catch (DataIntegrityViolationException e) {

            return false;
        }
    }

    private void publicateAndSaveCacheOrder(Order order, UUID reservationKey){

        List<OrderItemEvent> items =
                order.getItems()
                        .stream()
                        .map(item ->
                                new OrderItemEvent(
                                        item.getProductId(),
                                        item.getQuantity()
                                ))
                        .toList();

        OrderCreatedEvent orderCreatedEvent =
                new OrderCreatedEvent(
                        UUID.randomUUID().toString(),
                        MDC.get("requestId"),
                        order.getId(),
                        order.getUserId(),
                        order.getTotalAmount(),
                        items,
                        reservationKey
                );

        outboxService.publishCreated(orderCreatedEvent);

        try {
            cacheService.save(order);
        }
        catch (Exception ignored){
        }

    }


    public OrderDto createOrder(CreateOrderRequest request) {

        Long userId = securityService.getCurrentUserId();

        CartResponse cart =
                cartService.getValidatedCart();

        CheckProductRequest requestCheck = new CheckProductRequest();

        Map<Long, Integer> products = cart.getItems()
                .stream()
                .collect(Collectors.toMap(
                        CartItemResponse::getProductId,
                        CartItemResponse::getQuantity
                ));

        requestCheck.setProducts(products);
        
        CheckProductResponse productResponse = productService.getAvailability(requestCheck);

        Map<Long, ProductAvailability> productAvailability =
                productResponse.getProductAvailability();

        for (Map.Entry<Long, ProductAvailability> entry : productAvailability.entrySet()) {

            Long productId = entry.getKey();
            ProductAvailability availability = entry.getValue();

            if (!availability.isExists()) {
                throw new ProductNotFoundException(
                        "Product not found: " + productId
                );
            }

            if (!availability.isEnoughStock()) {
                throw new ProductOutOfStockException(
                        "Not enough stock for product: " + productId
                );
            }
        }

        List<Long> ids = cart.getItems()
                .stream()
                .map(CartItemResponse::getProductId)
                .toList();

        Map<Long, BigDecimal> prices =
                productService.loadPrices(ids);

        Order order =
                builderService.build(
                        cart,
                        prices,
                        userId
                );

        deliveryService.attachDelivery(
                order,
                request,
                userId
        );

        Order saved =
                orderRepository.save(order);

        publicateAndSaveCacheOrder(saved, null);

        return orderMapper.toDto(saved);
    }

    public void awaitingPayment(
            OrderAwaitingPaymentEvent event
    ){

        if (!markProcessed(event.eventId())) {

            log.warn(
                    "DUPLICATE_ORDER_AWAITING_PAYMENT_SKIPPED eventId={}",
                    event.eventId()
            );

            return;
        }

        Order order =
                orderRepository.findById(
                        event.orderId()
                ).orElseThrow();

        if(order.getOrderStatus() != OrderStatus.CREATED){

            log.info("ORDER_STATUS_NOT_CREATED");

            return;

        }

        order.setOrderStatus(
                OrderStatus.AWAITING_PAYMENT
        );

        orderRepository.save(order);
    }

    public void confirm(
            OrderConfirmedEvent event
    ){

        if (!markProcessed(event.eventId())) {

            log.warn(
                    "DUPLICATE_ORCHESTOR_ORDER_CONFIRMED_SKIPPED eventId={}",
                    event.eventId()
            );

            return;
        }

        Order order =
                orderRepository.findById(
                        event.orderId()
                ).orElseThrow();

        if(order.getOrderStatus() != OrderStatus.AWAITING_PAYMENT){
            return;
        }

        order.setOrderStatus(
                OrderStatus.CONFIRMED
        );

        outboxService.publishConfirmed(event);

    }

    public void cancel(
            OrderCancelledEvent event
    ) {


        if (!markProcessed(event.eventId())) {

            log.warn(
                    "KAFKA_DUPLICATE_EVENT_SKIPPED eventId={}",
                    event.eventId()
            );

            return;
        }


        Order order =
                orderRepository.findById(
                        event.orderId()
                ).orElseThrow();

        order.setOrderStatus(
                OrderStatus.CANCELLED
        );

        outboxService.publishCancelled(event);

        log.info(
                "ORDER_CANCELLED orderId={}",
                order.getId()
        );

    }

    public void createOrderByFlashSale(FlashSaleReservationAndCheckoutEvent event) {

        if (!markProcessed(event.eventId().toString())) {

            log.warn(
                    "DUPLICATE_FLASHSALE_RESERVATION_SKIPPED eventId={}",
                    event.eventId()
            );

            return;
        }

        Order order = builderService.buildFlashSaleOrder(
                event,
                event.discountedPrice()
        );

        Order saved =
                orderRepository.save(order);

        publicateAndSaveCacheOrder(saved, event.reservationKey());

    }
}
