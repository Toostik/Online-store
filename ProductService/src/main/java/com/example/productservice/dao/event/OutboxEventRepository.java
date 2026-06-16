package com.example.productservice.dao.event;


import com.example.productservice.entity.event.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {


}
