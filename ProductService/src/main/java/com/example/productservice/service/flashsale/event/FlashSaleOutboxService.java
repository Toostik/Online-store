package com.example.productservice.service.flashsale.event;

import com.example.productservice.dao.event.OutboxEventRepository;
import com.example.productservice.entity.event.OutboxEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.flashsale.FlashSaleReservationAndCheckoutEvent;
import org.example.events.flashsale.FlashSaleReservationExpiredEvent;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlashSaleOutboxService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    private void saveEvent(
            String type,
            String aggregateId,
            Object payload
    ) {

        OutboxEvent outbox =
                OutboxEvent.builder()
                        .aggregateType("flashsale")
                        .aggregateId(aggregateId)
                        .type(type)
                        .payload(
                                objectMapper.valueToTree(payload)
                        )
                        .build();

        repository.save(outbox);

    }

    public void publishCreated(FlashSaleReservationAndCheckoutEvent event) {

        saveEvent(
                "flashsale.created",
                event.flashSaleId().toString(),
                event
        );
    }

    public void publishExpired(FlashSaleReservationExpiredEvent event) {

        saveEvent(
                "flashsale.expired",
                event.flashSaleId().toString(),
                event
        );

    }

}
