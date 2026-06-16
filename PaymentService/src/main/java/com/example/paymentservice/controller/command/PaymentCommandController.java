package com.example.paymentservice.controller.command;

import com.example.paymentservice.dto.payment.request.CreatePaymentRequest;
import com.example.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentCommandController {

    private final PaymentService paymentService;

    @PostMapping("/orders/{orderId}")
    public ResponseEntity<Void> payOrder(
            @PathVariable Long orderId,
            @RequestBody CreatePaymentRequest request
    ){
        paymentService.pay(
                orderId,
                request
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/failed/{orderId}")
    public ResponseEntity<Void> failedPay(
            @PathVariable Long orderId,
            @RequestBody CreatePaymentRequest request
    ){
        paymentService.failedPay(
                orderId,
                request
        );

        return ResponseEntity.ok().build();
    }
}
