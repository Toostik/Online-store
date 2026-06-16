package com.example.notificationservice.dao.event;



import com.example.notificationservice.entity.event.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {


}
