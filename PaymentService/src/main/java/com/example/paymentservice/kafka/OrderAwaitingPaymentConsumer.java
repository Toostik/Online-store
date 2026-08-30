package com.example.paymentservice.kafka;

import com.example.paymentservice.service.payment.PaymentService;
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

    private final PaymentService paymentService;

    @KafkaListener(
            topics = "order-orchestrator-service.order.awaiting.payment",
            groupId = "payment-consumers-group",
            containerFactory = "orderAwaitingPaymentKafkaListenerContainerFactory"
    )
    public void consume(
            OrderAwaitingPaymentEvent event,
            Acknowledgment ack
    ){

        try {

            log.info("ORDER_AWAITING_RECEIVED -> {}", event.orderId());

            paymentService.awaitingPayment(event);

            ack.acknowledge();

        }
        catch (Exception e){

            throw e;

        }

    }

}
