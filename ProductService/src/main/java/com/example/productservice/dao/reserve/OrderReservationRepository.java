package com.example.productservice.dao.reserve;

import com.example.productservice.entity.reserve.OrderReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderReservationRepository
        extends JpaRepository<OrderReservation, Long> {

    Optional<OrderReservation> findByOrderId(
            Long orderId
    );

}