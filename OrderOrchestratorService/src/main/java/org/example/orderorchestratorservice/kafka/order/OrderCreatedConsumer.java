package org.example.orderorchestratorservice.kafka.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.order.OrderCreatedEvent;
import org.example.orderorchestratorservice.service.saga.SagaService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedConsumer {

    private final SagaService sagaService;

    @KafkaListener(
            topics = "order-service.order.created",
            groupId = "order-orchestrator-group",
            containerFactory = "orderCreatedKafkaListenerContainerFactory"
    )
    public void consume(
            OrderCreatedEvent event,
            Acknowledgment ack
    ) {

        try {

            log.info(
                    "ORDER_CREATED_RECEIVED orderId={} eventId={}",
                    event.orderId(),
                    event.eventId()
            );

            sagaService.process(event);

            ack.acknowledge();

        } catch (Exception e) {

            log.error(
                    "ORDER_CREATED_PROCESSING_ERROR orderId={} eventId={}",
                    event.orderId(),
                    event.eventId(),
                    e
            );

            throw e;
        }
    }

}