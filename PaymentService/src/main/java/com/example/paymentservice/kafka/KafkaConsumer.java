package com.example.paymentservice.kafka;

import com.example.paymentservice.dto.OrderDto;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final PaymentService paymentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "orders-created", groupId = "payment-consumers-group")
    public void consume(OrderDto orderDto, Acknowledgment ack){
        paymentService.createPayment(orderDto);
        ack.acknowledge();
        LOGGER.info("Order received -> {}", orderDto.getId());
    }

}
