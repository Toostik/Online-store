package com.example.notificationservice.dao.event;


import com.example.notificationservice.entity.event.ProcessedEvent;
import org.springframework.data.repository.CrudRepository;

public interface ProcessedEventRepository extends CrudRepository<ProcessedEvent, String> {
}
