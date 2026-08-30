package com.example.orderservice.kafka;

import com.example.orderservice.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.order.OrderAwaitingPaymentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderAwaitingPaymentConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "order-orchestrator-service.order.awaiting.payment",
            groupId = "orders-consumers-group",
            containerFactory = "orderAwaitingPaymentKafkaListenerContainerFactory"
    )
    public void consume(
            OrderAwaitingPaymentEvent event,
            Acknowledgment ack
    ){

        try {
            log.info("ORDER_AWAITING_RECEIVED -> {}", event.orderId());
            orderService.awaitingPayment(event);

            ack.acknowledge();

        }
        catch (Exception e){

            throw e;

        }

    }

}
