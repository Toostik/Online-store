package com.example.orderservice.service.order.status;

import com.example.orderservice.dao.order.OrderRepository;
import com.example.orderservice.dto.payment.PaymentDto;
import com.example.orderservice.entity.order.Order;
import com.example.orderservice.entity.enums.OrderStatus;
import com.example.orderservice.exceptions.order.OrderNotFoundException;
import com.example.orderservice.service.order.cache.OrderCacheService;
import com.example.orderservice.service.order.event.OrderOutboxService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderRepository orderRepository;
    private final OrderCacheService cacheService;
    private final OrderOutboxService eventService;

    @Transactional
    public void confirm(PaymentDto paymentDto){

        Order order =
                orderRepository.findById(
                                paymentDto.getOrderId()
                        )
                        .orElseThrow(
                                () -> new OrderNotFoundException(
                                        paymentDto.getOrderId()
                                )
                        );

        if(order.getOrderStatus() == OrderStatus.CONFIRMED)
            return;

        order.setOrderStatus(OrderStatus.CONFIRMED);

        cacheService.save(order);

//        OrderConfirmedEvent event = new OrderConfirmedEvent(
//                UUID.randomUUID().toString(),
//
//
//        );
//
//
//        eventService.publishConfirmed();

    }

}
