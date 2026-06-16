package org.example.orderorchestratorservice.dao.event;


import org.example.orderorchestratorservice.entity.event.ProcessedEvent;
import org.springframework.data.repository.CrudRepository;

public interface ProcessedEventRepository extends CrudRepository<ProcessedEvent, String> {
}
