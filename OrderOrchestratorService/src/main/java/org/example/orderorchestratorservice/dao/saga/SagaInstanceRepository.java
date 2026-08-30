package org.example.orderorchestratorservice.dao.saga;

import jakarta.persistence.LockModeType;
import org.example.orderorchestratorservice.entity.SagaInstance;
import org.example.orderorchestratorservice.entity.enums.SagaStatus;
import org.example.orderorchestratorservice.entity.enums.SagaStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SagaInstance> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    List<SagaInstance> findByStatusAndCurrentStepAndUpdatedAtBefore(
            SagaStatus status,
            SagaStep currentStep,
            LocalDateTime updatedAt
    );
}
