package com.example.orderservice.kafka.flashsale;

import com.example.orderservice.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.flashsale.FlashSaleReservationAndCheckoutEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashSaleReservationCreatedConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "product-service.flashsale.created",
            groupId = "orders-consumers-group",
            containerFactory = "flashSaleReservationAndCheckoutEventKafkaListenerContainerFactory"
    )
    public void consume(
            FlashSaleReservationAndCheckoutEvent event,
            Acknowledgment ack
    ){

        try {

            orderService.createOrderByFlashSale(event);

            ack.acknowledge();

        }
        catch (Exception e){

            throw e;

        }

    }

}
