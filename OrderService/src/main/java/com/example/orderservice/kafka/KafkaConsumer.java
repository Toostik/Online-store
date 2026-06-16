package com.example.orderservice.kafka;

import com.example.orderservice.dao.event.ProcessedEventRepository;
import com.example.orderservice.dto.payment.PaymentDto;
import com.example.orderservice.entity.event.ProcessedEvent;

import com.example.orderservice.service.order.status.OrderStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final OrderStatusService orderStatusService;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(
            topics = "payment-completed",
            groupId = "order-consumers-group"
    )

    public void consume(
            PaymentDto paymentDto,
            Acknowledgment ack
    ) {

        try {

            if (processedEventRepository.existsById(
                    paymentDto.getEventId()
            )) {
                ack.acknowledge();
                return;
            }

            orderStatusService.confirm(paymentDto);

            processedEventRepository.save(
                    new ProcessedEvent(
                            paymentDto.getEventId()
                    )
            );

            ack.acknowledge();

        } catch (Exception e) {
            throw e;
        }
    }

//    @KafkaListener(topics = "cart-checkout", groupId = "order-consumers-group")
//    public void consumeCart(CartDto cartDto, Acknowledgment ack) {
//        try {
//            log.info("KAFKA_CART_RECEIVED cartId={} eventId={}",
//                    cartDto.getId(), cartDto.getEventId());
//
//            if(processedEventRepository.existsById(cartDto.getEventId())){
//                log.warn("KAFKA_EVENT_DUPLICATE eventId={}", cartDto.getEventId());
//                ack.acknowledge();
//                return;
//            }
//
//            orderService.createOrder();
//            log.info("ORDER_CREATED_FROM_CART userId={}", cartDto.getUserId());
//
//            processedEventRepository.save(new ProcessedEvent(cartDto.getEventId()));
//
//            ack.acknowledge();
//            log.info("KAFKA_CART_PROCESSED eventId={}", cartDto.getEventId());
//
//        } catch (Exception e) {
//            log.error("KAFKA_NOTIFICATION_ERROR orderId={} eventId={}",
//                    cartDto.getId(), cartDto.getEventId(), e);
//            throw e;
//        }
//    }

}
