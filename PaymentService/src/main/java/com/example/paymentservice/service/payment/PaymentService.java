package com.example.paymentservice.service.payment;

import com.example.paymentservice.dao.payment.PaymentRepository;
import com.example.paymentservice.dto.payment.request.CreatePaymentRequest;
import com.example.paymentservice.entity.enums.Status;
import com.example.paymentservice.service.payment.command.PaymentCommandService;
import com.example.paymentservice.service.payment.query.PaymentQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentCommandService commandService;
    private final PaymentQueryService queryService;

    public void pay(
            Long orderId,
            CreatePaymentRequest request
    ) {

        commandService.pay(
                orderId,
                request
        );

    }

    public void failedPay(
            Long orderId,
            CreatePaymentRequest request
    ) {

        commandService.failedPay(
                orderId,
                request
        );

    }

}
