package org.example.orderorchestratorservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.orderorchestratorservice.entity.enums.SagaStatus;
import org.example.orderorchestratorservice.entity.enums.SagaStep;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "saga_instances",
        indexes = {
                @Index(name = "idx_saga_order_id", columnList = "order_id"),
                @Index(name = "idx_saga_status", columnList = "status"),
                @Index(name = "idx_saga_step", columnList = "current_step"),
                @Index(name = "idx_saga_updated_at", columnList = "updated_at")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID sagaId;

    @Column(nullable = false, unique = true)
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_step", nullable = false)
    private SagaStep currentStep;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private Long version;

}