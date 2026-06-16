package com.example.paymentservice.dao.payment;

import com.example.paymentservice.entity.payment.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

    Payment findByTransactionId(String transactionId);

    Optional<Payment> getPaymentByTransactionId(String id);

    List<Payment> findAllByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);
}
