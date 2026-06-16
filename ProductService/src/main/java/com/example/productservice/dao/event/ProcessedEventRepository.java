package com.example.productservice.dao.event;

import com.example.productservice.entity.event.ProcessedEvent;
import org.springframework.data.repository.CrudRepository;

public interface ProcessedEventRepository extends CrudRepository<ProcessedEvent, String> {
}
