package com.example.paymentservice.entity;

import com.example.paymentservice.dto.PaymentDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    @Column(name = "transaction_id")
    private String transactionId = UUID.randomUUID().toString();

    public PaymentDto toDto(){
        return new PaymentDto(
                id,
                userId,
                orderId,
                status,
                amount,
                transactionId
        );
    }
}
