package com.example.cartservice.service.event;

import com.example.cartservice.dao.event.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxCleanupScheduler {

    private final OutboxEventRepository repository;

    @Transactional
    @Scheduled(cron = "0 30 3 * * *")
    @SchedulerLock(
            name = "outboxCleanup",
            lockAtMostFor = "30m"
    )
    public void cleanup() {

        Instant threshold = Instant.now().minus(Duration.ofDays(30));

        int total = 0;

        while (true) {

            int deleted =
                    repository.deleteBatch(threshold, 5000);

            total += deleted;

            if (deleted < 5000) {
                break;
            }
        }

        log.info("Deleted {} outbox events", total);
    }
}
