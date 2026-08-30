package com.example.cartservice.dao.event;


import com.example.cartservice.entity.event.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Transactional
    @Modifying
    @Query(value = """
        DELETE FROM outbox_events
        WHERE id IN (
            SELECT id
            FROM outbox_events
            WHERE created_at < :createdAt
            LIMIT :batchSize
        )
        """, nativeQuery = true)
    int deleteBatch(
            Instant createdAt,
            int batchSize
    );

}
