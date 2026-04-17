package com.example.paymentservice.service;

import com.example.paymentservice.dao.PaymentRepository;
import com.example.paymentservice.dto.OrderDto;
import com.example.paymentservice.dto.PaymentDto;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.entity.Status;
import com.example.paymentservice.kafka.KafkaProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaProducer kafkaProducer;
    private final RedisTemplate<String, Object> redisTemplate;

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

            kafkaProducer.sendMessage( "payment-completed", payment.toDto());
        } else {
            payment.setStatus(Status.FAILED);
            paymentRepository.save(payment);

            kafkaProducer.sendMessage("payment-failed", payment.toDto());
        }
    }

    public PaymentDto getPaymentByTransactionId(String id) {
        Payment payment = paymentRepository.getPaymentByTransactionId(id).orElseThrow(
                () -> new RuntimeException("Payment not found!")
        );

        return payment.toDto();
    }

    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Payment not found!")
        );
        return payment.toDto();
    }

    public List<PaymentDto> getAllPaymentsByOrderId(Long orderId) {
        return paymentRepository.findAllByOrderId(orderId)
                .stream()
                .map(Payment::toDto)
                .collect(Collectors.toList());
    }
}
