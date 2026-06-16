package com.example.paymentservice.dto.payment;

import com.example.paymentservice.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private Long userId;
    private Long orderId;
    private Status status;
    private BigDecimal amount;
    private String transactionId;
    private String eventId;

    public PaymentDto(Long id, Long userId, Long orderId, Status status, BigDecimal amount, String transactionId) {
        this.id = id;
        this.userId = userId;
        this.orderId = orderId;
        this.status = status;
        this.amount = amount;
        this.transactionId = transactionId;
    }
}
