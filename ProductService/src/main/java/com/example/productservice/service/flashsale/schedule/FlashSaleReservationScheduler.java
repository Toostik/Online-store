package com.example.productservice.service.flashsale.schedule;

import com.example.productservice.dao.flashsale.FlashSaleReservationRepository;
import com.example.productservice.entity.flashsale.FlashSaleReservation;
import com.example.productservice.entity.enums.ReservationStatus;
import com.example.productservice.service.flashsale.cache.FlashSaleReserveCacheService;
import com.example.productservice.service.flashsale.event.FlashSaleOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashSaleReservationScheduler {

    private final FlashSaleReservationRepository flashSaleReservationRepository;
    private final FlashSaleReserveCacheService flashSaleReserveCacheService;
    private final FlashSaleOutboxService flashSaleOutboxService;

    @Scheduled(fixedDelay = 30000)
    @Transactional
    @SchedulerLock(
            name = "expireReservations",
            lockAtMostFor = "2m",
            lockAtLeastFor = "10s"
    )
    public void expireReservations() {

        List<FlashSaleReservation> flashSaleReservations = flashSaleReservationRepository
                .findByStatusAndExpiresAtBefore(ReservationStatus.PENDING, Instant.now());

        for (FlashSaleReservation flashSaleReservation : flashSaleReservations) {

            if (flashSaleReservation.getStatus() != ReservationStatus.PENDING) {
                continue;
            }

            flashSaleReservation.setStatus(ReservationStatus.EXPIRED);

//            Long flashSaleId = flashSaleReservation.getFlashSale().getId();
//            Integer quantity = flashSaleReservation.getQuantity();
//
//            flashSaleReserveCacheService.releaseReservation(flashSaleId, quantity);
//
//            FlashSaleReservationExpiredEvent event = FlashSaleReservationExpiredEvent.builder()
//                    .reservationKey(flashSaleReservation.getReservationKey())
//                    .flashSaleId(flashSaleId)
//                    .userId(flashSaleReservation.getUserId())
//                    .quantity(quantity)
//                    .build();
//
//            flashSaleOutboxService.publishExpired(event);

            log.info(
                    "Expired reservation {}",
                    flashSaleReservation.getReservationKey()
            );

        }

    }

}
