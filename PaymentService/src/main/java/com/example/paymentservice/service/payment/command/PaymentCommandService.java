package com.example.paymentservice.service.payment.command;

import com.example.paymentservice.dao.event.ProcessedEventRepository;
import com.example.paymentservice.dao.payment.PaymentRepository;
import com.example.paymentservice.dto.payment.request.CreatePaymentRequest;

import com.example.paymentservice.entity.enums.Status;
import com.example.paymentservice.entity.event.ProcessedEvent;
import com.example.paymentservice.entity.payment.Payment;
import com.example.paymentservice.exceptions.PaymentNotFoundException;
import com.example.paymentservice.service.event.PaymentOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.enums.PaymentFailureReason;
import org.example.events.order.OrderAwaitingPaymentEvent;
import org.example.events.payment.PaymentCompletedEvent;
import org.example.events.payment.PaymentFailedEvent;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final ProcessedEventRepository processedEventRepository;

    private boolean markProcessed(String eventId) {

        try {

            ProcessedEvent save = processedEventRepository.save(
                    new ProcessedEvent(eventId)
            );

            return true;
        }
        catch (DataIntegrityViolationException e) {

            return false;
        }
    }

    public void pay(
            Long orderId,
            CreatePaymentRequest request
    ) {

        Payment payment = paymentRepository.findByOrderIdAndStatus(
                orderId,
                Status.PROCESSING
        ).orElseThrow(
                () -> new PaymentNotFoundException("Payment not found")
        );

        payment.setPaymentMethod(request.paymentMethod());
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

//        OrderPaymentInfoResponse order = orderService.getOrderPaymentInfo(orderId);

        Payment payment = paymentRepository.findByOrderIdAndStatus(
                orderId,
                Status.PROCESSING
        ).orElseThrow(
                () -> new PaymentNotFoundException("Payment not found")
        );

        payment.setPaymentMethod(request.paymentMethod());
        payment.setStatus(
                Status.FAILED
        );

        PaymentFailedEvent event =
                new PaymentFailedEvent(
                        UUID.randomUUID().toString(),
                        MDC.get("requestId"),
                        payment.getOrderId(),
                        payment.getUserId(),
                        PaymentFailureReason.PAYMENT_DECLINED
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

    public void awaitingPayment(OrderAwaitingPaymentEvent event) {

        if (!markProcessed(event.eventId())) {

            log.warn(
                    "DUPLICATE_AWAITING_PAYMENT_ORDER_SKIPPED eventId={}",
                    event.eventId()
            );

            return;
        }

        Payment payment = Payment.builder()
                .userId(event.userId())
                .orderId(event.orderId())
                .status(Status.PROCESSING)
                .amount(event.totalAmount())
                .transactionId(UUID.randomUUID().toString())
                .build();

        paymentRepository.save(payment);

    }

}
