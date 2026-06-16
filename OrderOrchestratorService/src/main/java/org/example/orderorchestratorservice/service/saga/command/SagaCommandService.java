package org.example.orderorchestratorservice.service.saga.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.inventory.*;
import org.example.events.order.OrderAwaitingPaymentEvent;
import org.example.events.order.OrderCancelledEvent;
import org.example.events.order.OrderConfirmedEvent;
import org.example.events.order.OrderCreatedEvent;
import org.example.events.payment.PaymentCompletedEvent;
import org.example.events.payment.PaymentFailedEvent;
import org.example.orderorchestratorservice.dao.event.ProcessedEventRepository;
import org.example.orderorchestratorservice.dao.saga.SagaInstanceRepository;
import org.example.orderorchestratorservice.entity.SagaInstance;
import org.example.orderorchestratorservice.entity.enums.SagaStatus;
import org.example.orderorchestratorservice.entity.enums.SagaStep;
import org.example.orderorchestratorservice.entity.event.ProcessedEvent;
import org.example.orderorchestratorservice.exceptions.SagaNotFoundException;
import org.example.orderorchestratorservice.service.event.SagaOutboxService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SagaCommandService {


    private final SagaInstanceRepository sagaRepository;
    private final SagaOutboxService outboxService;
    private final ProcessedEventRepository processedEventRepository;

    private boolean markProcessed(String eventId) {

        try {

            processedEventRepository.save(
                    new ProcessedEvent(eventId)
            );

            return true;
        }
        catch (DataIntegrityViolationException e) {

            return false;
        }
    }

    public void process(OrderCreatedEvent event) {

        if (!markProcessed(event.eventId())) {

            log.warn(
                    "DUPLICATE_INVENTORY_RESERVED_SKIPPED eventId={}",
                    event.eventId()
            );

            return;
        }

        if (sagaRepository.existsByOrderId(event.orderId())) {

            log.warn(
                    "DUPLICATE_SAGA_CREATE_SKIPPED orderId={}",
                    event.orderId()
            );

            return;
        }

        SagaInstance saga =
                SagaInstance.builder()
                        .sagaId(UUID.randomUUID())
                        .orderId(event.orderId())
                        .userId(event.userId())
                        .totalAmount(event.totalAmount())
                        .status(SagaStatus.STARTED)
                        .currentStep(SagaStep.WAITING_RESERVE)
                        .retryCount(0)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        sagaRepository.save(saga);

        InventoryReserveRequestedEvent reserveEvent =
                new InventoryReserveRequestedEvent(
                        UUID.randomUUID().toString(),
                        event.correlationId(),
                        event.orderId(),
                        event.items()
                );

        outboxService.publishInventoryReserveRequested(
                reserveEvent
        );

    }

    public void inventoryReserved(
            InventoryReservedEvent event
    ){

        if (!markProcessed(event.eventId())) {

            log.warn(
                    "DUPLICATE_INVENTORY_RESERVED_SKIPPED eventId={}",
                    event.eventId()
            );

            return;
        }

        SagaInstance saga =
                sagaRepository.findByOrderId(
                        event.orderId()
                ).orElseThrow(() ->
                        new SagaNotFoundException(
                                "Saga not found for orderId="
                                        + event.orderId()
                        )
                );

        if (saga.getCurrentStep() != SagaStep.WAITING_RESERVE) {

            log.warn(
                    "UNEXPECTED_SAGA_STATE orderId={} status={}",
                    saga.getOrderId(),
                    saga.getStatus()
            );

            return;
        }

        saga.setCurrentStep(
                SagaStep.WAITING_PAYMENT
        );

        saga.setUpdatedAt(
                LocalDateTime.now()
        );

        sagaRepository.save(saga);

        OrderAwaitingPaymentEvent paymentEvent =
                new OrderAwaitingPaymentEvent(
                        UUID.randomUUID().toString(),
                        event.correlationId(),
                        event.orderId()
                );

        outboxService.publishOrderAwaitingPayment(
                paymentEvent
        );

    }

    public void paymentCompleted(PaymentCompletedEvent event) {

        if (!markProcessed(event.eventId())) {
            return;
        }

        SagaInstance saga =
                sagaRepository.findByOrderId(
                        event.orderId()
                ).orElseThrow();

        if (saga.getCurrentStep() != SagaStep.WAITING_PAYMENT) {
            return;
        }

        saga.setCurrentStep(
                SagaStep.WAITING_COMMIT
        );

        saga.setUpdatedAt(
                LocalDateTime.now()
        );

        sagaRepository.save(saga);

        InventoryCommitRequestedEvent commitEvent =
                new InventoryCommitRequestedEvent(
                        UUID.randomUUID().toString(),
                        event.correlationId(),
                        event.orderId(),
                        event.userId(),
                        event.amount()
                );

        outboxService.publishInventoryCommitRequest(
                commitEvent
        );
    }

    public void paymentFailed(
            PaymentFailedEvent event
    ) {

        if (!markProcessed(event.eventId())) {

            log.warn(
                    "DUPLICATE_PAYMENT_FAILED_SKIPPED eventId={}",
                    event.eventId()
            );

            return;
        }

        SagaInstance saga =
                sagaRepository.findByOrderId(
                        event.orderId()
                ).orElseThrow();

        if (saga.getCurrentStep() != SagaStep.WAITING_PAYMENT) {

            log.warn(
                    "UNEXPECTED_SAGA_STATE orderId={} step={}",
                    saga.getOrderId(),
                    saga.getCurrentStep()
            );

            return;
        }

        saga.setCurrentStep(
                SagaStep.WAITING_RELEASE
        );

        saga.setUpdatedAt(
                LocalDateTime.now()
        );

        sagaRepository.save(saga);

        InventoryReleaseRequestedEvent releaseEvent =
                new InventoryReleaseRequestedEvent(
                        UUID.randomUUID().toString(),
                        event.correlationId(),
                        event.orderId()
                );

        outboxService.publishInventoryReleaseRequested(
                releaseEvent
        );

    }

    public void inventoryReleased(
            InventoryReleasedEvent event
    ) {

        try {

            processedEventRepository.save(
                    new ProcessedEvent(event.eventId())
            );

        }
        catch (DataIntegrityViolationException e) {

            log.warn(
                    "KAFKA_DUPLICATE_EVENT_SKIPPED eventId={}",
                    event.eventId()
            );

            return;
        }

        SagaInstance saga =
                sagaRepository.findByOrderId(
                        event.orderId()
                ).orElseThrow();

        saga.setStatus(
                SagaStatus.FAILED
        );

        saga.setCurrentStep(
                SagaStep.FAILED
        );

        saga.setUpdatedAt(
                LocalDateTime.now()
        );

        sagaRepository.save(
                saga
        );

        OrderCancelledEvent cancelledEvent =
                new OrderCancelledEvent(
                        UUID.randomUUID().toString(),
                        event.correlationId(),
                        event.orderId(),
                        saga.getUserId(),
                        "PAYMENT_FAILED"
                );

        outboxService.publishCancelled(
                cancelledEvent
        );

        log.info(
                "ORDER_CANCELLED_PUBLISHED orderId={}",
                event.orderId()
        );

    }

    @Transactional
    public void inventoryCommitted(
            InventoryCommittedEvent event
    ) {

        try {

            processedEventRepository.save(
                    new ProcessedEvent(event.eventId())
            );

        }
        catch (DataIntegrityViolationException e) {

            log.warn(
                    "KAFKA_DUPLICATE_EVENT_SKIPPED eventId={}",
                    event.eventId()
            );

            return;
        }

        SagaInstance saga =
                sagaRepository.findByOrderId(
                        event.orderId()
                ).orElseThrow();

        saga.setStatus(
                SagaStatus.COMPLETED
        );

        saga.setCurrentStep(
                SagaStep.COMPLETED
        );

        saga.setUpdatedAt(
                LocalDateTime.now()
        );

        OrderConfirmedEvent confirmedEvent =
                new OrderConfirmedEvent(
                        UUID.randomUUID().toString(),
                        event.correlationId(),
                        event.orderId(),
                        event.userId(),
                        event.amount()
                );

        outboxService.publishConfirmed(
                confirmedEvent
        );

    }

}