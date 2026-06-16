package com.example.paymentservice.dao.event;



import com.example.paymentservice.entity.event.ProcessedEvent;
import org.springframework.data.repository.CrudRepository;

public interface ProcessedEventRepository extends CrudRepository<ProcessedEvent, String> {
}
