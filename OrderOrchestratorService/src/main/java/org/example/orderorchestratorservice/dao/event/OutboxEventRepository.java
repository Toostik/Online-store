package org.example.orderorchestratorservice.dao.event;


import org.example.orderorchestratorservice.entity.event.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {


}
