package com.example.paymentservice.service;

import com.example.paymentservice.dao.PaymentRepository;
import com.example.paymentservice.dto.OrderDto;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.entity.Status;
import com.example.paymentservice.kafka.KafkaProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaProducer kafkaProducer;

    public void createPayment(OrderDto orderDto) {

        if(orderDto.getStatus().equals("CONFIRMED")){
            throw new RuntimeException("Order is payed");
        }

        Payment payment = new Payment();

        payment.setOrderId(orderDto.getId());
        payment.setUserId(orderDto.getUserId());
        payment.setStatus(Status.CREATED);
        payment.setAmount(orderDto.getTotalAmount());

        paymentRepository.save(payment);

        boolean success = true;

        if (success) {
            payment.setStatus(Status.PAYED);
            paymentRepository.save(payment);

            kafkaProducer.sendMessage( "orders-paid", payment.toDto());
        } else {
            payment.setStatus(Status.FAILED);
            paymentRepository.save(payment);

            kafkaProducer.sendMessage("orders-failed", payment.toDto());
        }
    }
}
