package com.example.cartservice.dao.event;


import com.example.cartservice.entity.event.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {


}
