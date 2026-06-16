package com.example.notificationservice.kafka;

import com.example.notificationservice.service.notification.NotificationService;
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

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "order-service.order.confirmed",
            groupId = "notifications-consumers-group",
            containerFactory = "orderConfirmedKafkaListenerContainerFactory"
    )
    public void consume(
            OrderConfirmedEvent event,
            Acknowledgment ack
    ) {

        try {

            notificationService.handleOrderConfirmed(event);

            ack.acknowledge();

        } catch (Exception e) {

            log.error(
                    "Failed to process order confirmed event. orderId={}",
                    event.orderId(),
                    e
            );

            throw e;
        }

    }

}