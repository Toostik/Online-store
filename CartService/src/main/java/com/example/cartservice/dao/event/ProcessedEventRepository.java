package com.example.cartservice.dao.event;


import com.example.cartservice.entity.event.ProcessedEvent;
import org.springframework.data.repository.CrudRepository;

public interface ProcessedEventRepository extends CrudRepository<ProcessedEvent, String> {
}
