package com.example.paymentservice.dao;

import com.example.paymentservice.dto.PaymentDto;
import com.example.paymentservice.entity.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Period;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

    Payment findByTransactionId(String transactionId);

    Optional<Payment> getPaymentByTransactionId(String id);

    List<Payment> findAllByOrderId(Long orderId);
}
