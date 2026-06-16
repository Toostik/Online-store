package com.example.orderservice.dao.event;


import com.example.orderservice.entity.event.ProcessedEvent;
import org.springframework.data.repository.CrudRepository;

public interface ProcessedEventRepository extends CrudRepository<ProcessedEvent, String> {
}
