package com.example.orderservice.kafka;

import com.example.orderservice.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.order.OrderConfirmedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConfirmedConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "order-orchestrator-service.order.confirmed",
            groupId = "orders-consumers-group",
            containerFactory = "orderConfirmedKafkaListenerContainerFactory"
    )
    public void consume(
            OrderConfirmedEvent event,
            Acknowledgment ack
    ) {

        try {

            orderService.confirm(event);

            ack.acknowledge();

        } catch (Exception e) {

            throw e;

        }

    }

}
