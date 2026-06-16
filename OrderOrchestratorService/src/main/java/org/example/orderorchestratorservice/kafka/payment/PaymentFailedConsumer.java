package org.example.orderorchestratorservice.kafka.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.payment.PaymentFailedEvent;
import org.example.orderorchestratorservice.service.saga.SagaService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentFailedConsumer {

    private final SagaService sagaService;

    @KafkaListener(
            topics = "payment-service.payment.failed",
            groupId = "order-orchestrator-consumers-group",
            containerFactory = "paymentFailedKafkaListenerContainerFactory"
    )
    public void consume(
            PaymentFailedEvent event,
            Acknowledgment ack
    ) {

        try {

            sagaService.paymentFailed(event);

            ack.acknowledge();

        }
        catch (Exception e) {

            log.error(
                    "Failed to process payment failed event. orderId={}",
                    event.orderId(),
                    e
            );

            throw e;
        }

    }

}
