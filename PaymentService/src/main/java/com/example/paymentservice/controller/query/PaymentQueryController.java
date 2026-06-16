package com.example.paymentservice.controller.query;

import com.example.paymentservice.dto.payment.PaymentDto;
import com.example.paymentservice.service.payment.query.PaymentQueryService;
import com.example.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentQueryController {

    private final PaymentService paymentService;
    private final PaymentQueryService paymentQueryService;

    @GetMapping("/transaction/{id}")
    public ResponseEntity<PaymentDto> getPaymentByTransactionId(@PathVariable("id") String transactionId){
        log.info("PAYMENT_GET_BY_TRANSACTION transactionId={}", transactionId);
        return ResponseEntity.ok(paymentQueryService.getPaymentByTransactionId(transactionId));
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<List<PaymentDto>> getAllPaymentsByOrderId(@PathVariable(name = "id") Long orderId){
        log.info("PAYMENT_GET_BY_ORDER orderId={}", orderId);
        return ResponseEntity.ok(paymentQueryService.getAllPaymentsByOrderId(orderId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id){
        log.info("PAYMENT_GET_BY_ID id={}", id);
        return ResponseEntity.ok(paymentQueryService.getPaymentById(id));
    }






}
