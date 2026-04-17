package com.example.paymentservice.controller;

import com.example.paymentservice.dto.PaymentDto;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/transaction/{id}")
    public ResponseEntity<PaymentDto> getPaymentByTransactionId(@PathVariable("id") String transactionId){
        return ResponseEntity.ok(paymentService.getPaymentByTransactionId(transactionId));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id){
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<List<PaymentDto>> getAllPaymentsByOrderId(@PathVariable("id") Long orderId){
        return ResponseEntity.ok(paymentService.getAllPaymentsByOrderId(orderId));
    }



}
