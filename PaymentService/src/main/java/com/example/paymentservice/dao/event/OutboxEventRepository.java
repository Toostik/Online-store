package com.example.paymentservice.dao.event;


import com.example.paymentservice.entity.event.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {


}
