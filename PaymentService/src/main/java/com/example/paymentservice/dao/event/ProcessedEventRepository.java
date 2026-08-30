package com.example.paymentservice.dao.event;



import com.example.paymentservice.entity.event.ProcessedEvent;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;


public interface ProcessedEventRepository extends CrudRepository<ProcessedEvent, String> {
    @Modifying
    @Query(value = """
            DELETE FROM processed_events
            WHERE event_id IN (
                SELECT event_id
                FROM processed_events
                WHERE created_at < :createdAt
                LIMIT :batchSize
            )
            """, nativeQuery = true)
    int deleteBatch(
            Instant createdAt,
            int batchSize
    );
}
