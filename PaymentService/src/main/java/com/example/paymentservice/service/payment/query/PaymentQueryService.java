package com.example.paymentservice.service.payment.query;

import com.example.paymentservice.dao.payment.PaymentRepository;
import com.example.paymentservice.dto.payment.PaymentDto;
import com.example.paymentservice.entity.payment.Payment;
import com.example.paymentservice.exceptions.PaymentNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentQueryService {
    private final PaymentRepository paymentRepository;

    public PaymentDto getPaymentByTransactionId(String id) {
        log.debug("GET_PAYMENT_BY_TRANSACTION id={}", id);

        Payment payment = paymentRepository.getPaymentByTransactionId(id).orElseThrow(
                () -> new PaymentNotFoundException("Payment not found!")
        );

        return payment.toDto();
    }

    public PaymentDto getPaymentById(Long id) {
        log.debug("GET_PAYMENT_BY_ID id={}", id);
        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> new PaymentNotFoundException("Payment not found!")
        );
        return payment.toDto();
    }

    public List<PaymentDto> getAllPaymentsByOrderId(Long orderId) {
        log.debug("GET_PAYMENTS_BY_ORDER orderId={}", orderId);
        return paymentRepository.findAllByOrderId(orderId)
                .stream()
                .map(Payment::toDto)
                .collect(Collectors.toList());
    }
}
