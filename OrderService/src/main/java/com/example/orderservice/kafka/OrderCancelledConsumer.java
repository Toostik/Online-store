package com.example.orderservice.kafka;

import com.example.orderservice.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.order.OrderCancelledEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCancelledConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "order-orchestrator-service.order.cancelled",
            groupId = "orders-consumers-group",
            containerFactory = "orderCancelledKafkaListenerContainerFactory"
    )
    public void consume(
            OrderCancelledEvent event,
            Acknowledgment ack
    ) {

        try {

            orderService.cancel(event);

            ack.acknowledge();

        }
        catch (Exception e) {

            log.error(
                    "Failed to process order cancelled event. orderId={}",
                    event.orderId(),
                    e
            );

            throw e;
        }

    }

}
