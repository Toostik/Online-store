package com.example.paymentservice.service.payment.command;

import com.example.paymentservice.dao.payment.PaymentRepository;
import com.example.paymentservice.dto.order.request.OrderPaymentInfoResponse;
import com.example.paymentservice.dto.payment.event.PaymentCompletedEvent;
import com.example.paymentservice.dto.payment.event.PaymentFailedEvent;
import com.example.paymentservice.dto.payment.request.CreatePaymentRequest;
import com.example.paymentservice.entity.enums.FailureReason;
import com.example.paymentservice.entity.enums.PaymentFailureReason;
import com.example.paymentservice.entity.enums.Status;
import com.example.paymentservice.entity.payment.Payment;
import com.example.paymentservice.service.event.PaymentOutboxService;
import com.example.paymentservice.service.integration.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCommandService {

    private final PaymentRepository paymentRepository;
    private final PaymentOutboxService outboxService;
    private final OrderService orderService;

    public void pay(
            Long orderId,
            CreatePaymentRequest request
    ) {

        if (paymentRepository.existsByOrderId(orderId)) {
            throw new IllegalStateException(
                    "Payment already exists"
            );
        }

        OrderPaymentInfoResponse order = orderService.getOrderPaymentInfo(orderId);


        Payment payment = Payment.builder()
                .userId(order.userId())
                .orderId(order.orderId())
                .amount(order.totalAmount())
                .paymentMethod(request.paymentMethod())
                .status(Status.PROCESSING)
                .transactionId(UUID.randomUUID().toString())
                .build();

        paymentRepository.save(
                payment
        );

        // имитация успешной оплаты

        payment.setStatus(
                Status.COMPLETED
        );

        PaymentCompletedEvent event =
                new PaymentCompletedEvent(
                        UUID.randomUUID().toString(),
                        MDC.get("requestId"),
                        payment.getOrderId(),
                        payment.getUserId(),
                        payment.getAmount(),
                        payment.getTransactionId()
                );

        outboxService.publishCompleted(
                event
        );

        log.info(
                "PAYMENT_COMPLETED orderId={} transactionId={}",
                payment.getOrderId(),
                payment.getTransactionId()
        );

    }

    public void failedPay(
            Long orderId,
            CreatePaymentRequest request
    ) {

        if (paymentRepository.existsByOrderId(orderId)) {
            throw new IllegalStateException(
                    "Payment already exists"
            );
        }

        OrderPaymentInfoResponse order = orderService.getOrderPaymentInfo(orderId);


        Payment payment = Payment.builder()
                .userId(order.userId())
                .orderId(order.orderId())
                .amount(order.totalAmount())
                .paymentMethod(request.paymentMethod())
                .status(Status.PROCESSING)
                .transactionId(UUID.randomUUID().toString())
                .build();

        paymentRepository.save(
                payment
        );

        // имитация успешной оплаты

        payment.setStatus(
                Status.FAILED
        );

        PaymentFailedEvent event =
                new PaymentFailedEvent(
                        UUID.randomUUID().toString(),
                        MDC.get("requestId"),
                        payment.getOrderId(),
                        payment.getUserId(),
                        FailureReason.PAYMENT_DECLINED
                );

        outboxService.publishFailed(
                event
        );

        log.info(
                "PAYMENT_FAILED orderId={} transactionId={}",
                payment.getOrderId(),
                payment.getTransactionId()
        );

    }

}
