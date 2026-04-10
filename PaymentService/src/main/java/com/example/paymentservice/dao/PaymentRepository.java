package com.example.paymentservice.dao;

import com.example.paymentservice.entity.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

    Payment findByTransactionId(String transactionId);
}
