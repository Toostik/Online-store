package com.example.orderservice.kafka;

import com.example.orderservice.dto.PaymentDto;
import com.example.orderservice.entity.Status;
import com.example.orderservice.service.OrderService;

import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
=======
import org.apache.kafka.clients.consumer.internals.Acknowledgements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
>>>>>>> feature/kafka-redis-logging
import org.springframework.stereotype.Service;

import java.util.Stack;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final OrderService orderService;
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "orders-paid", groupId = "order-consumers-group")
<<<<<<< HEAD
    public void consume(PaymentDto paymentDto){
        LOGGER.info("Payment received -> {}", paymentDto.getId());
        orderService.updateStatus(Status.CONFIRMED, paymentDto);
=======
    public void consume(PaymentDto paymentDto, Acknowledgment ack){
        LOGGER.info("Payment received -> {}", paymentDto.getId());
        orderService.updateStatus(Status.CONFIRMED, paymentDto);
        ack.acknowledge();
>>>>>>> feature/kafka-redis-logging
    }

}
