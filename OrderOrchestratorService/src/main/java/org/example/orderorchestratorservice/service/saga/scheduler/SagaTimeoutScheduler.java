package org.example.orderorchestratorservice.service.saga.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.enums.PaymentFailureReason;
import org.example.events.payment.PaymentFailedEvent;
import org.example.orderorchestratorservice.dao.saga.SagaInstanceRepository;
import org.example.orderorchestratorservice.entity.SagaInstance;
import org.example.orderorchestratorservice.entity.enums.FailureReason;
import org.example.orderorchestratorservice.entity.enums.SagaStatus;
import org.example.orderorchestratorservice.entity.enums.SagaStep;
import org.example.orderorchestratorservice.service.event.SagaOutboxService;
import org.example.orderorchestratorservice.service.saga.SagaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SagaTimeoutScheduler {

    private final SagaInstanceRepository sagaRepository;
    private final SagaService sagaService;

    @Scheduled(fixedDelay = 300000)
    public void checkPaymentTimeouts() {

        List<SagaInstance> expiredSagas =
                sagaRepository.findByStatusAndCurrentStepAndUpdatedAtBefore(
                        SagaStatus.STARTED,
                        SagaStep.WAITING_PAYMENT,
                        LocalDateTime.now().minusMinutes(30)
                );

        if (expiredSagas.isEmpty()) {

            return;
        }

        log.warn(
                "Found {} expired sagas",
                expiredSagas.size()
        );

        for (SagaInstance saga : expiredSagas) {

            PaymentFailedEvent event =
                    new PaymentFailedEvent(
                            UUID.randomUUID().toString(),
                            saga.getSagaId().toString(),
                            saga.getOrderId(),
                            PaymentFailureReason.TIMEOUT
                    );

            sagaService.paymentFailed(event);

            log.warn(
                    "Payment timeout. orderId={}",
                    saga.getOrderId()
            );
        }

    }

}