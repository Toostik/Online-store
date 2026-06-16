package org.example.orderorchestratorservice.kafka.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.payment.PaymentCompletedEvent;
import org.example.orderorchestratorservice.service.saga.SagaService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCompletedConsumer {

    private final SagaService sagaService;

    @KafkaListener(
            topics = "payment-service.payment.completed",
            groupId = "order-orchestrator-group",
            containerFactory = "paymentCompletedKafkaListenerContainerFactory"
    )
    public void consume(
            PaymentCompletedEvent event,
            Acknowledgment ack
    ) {

        try {

            log.info(
                    "PAYMENT_COMPLETED_RECEIVED orderId={} eventId={}",
                    event.orderId(),
                    event.eventId()
            );

            sagaService.paymentCompleted(event);

            ack.acknowledge();

        }
        catch (Exception e) {

            log.error(
                    "PAYMENT_COMPLETED_ERROR orderId={} eventId={}",
                    event.orderId(),
                    event.eventId(),
                    e
            );

            throw e;
        }

    }

}
