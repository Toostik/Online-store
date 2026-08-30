package com.example.productservice.service.flashsale.schedule;

import com.example.productservice.dao.flashsale.FlashSaleRepository;
import com.example.productservice.dao.flashsale.FlashSaleReservationRepository;
import com.example.productservice.entity.flashsale.FlashSale;
import com.example.productservice.entity.enums.FlashSaleStatus;
import com.example.productservice.entity.enums.ReservationStatus;
import com.example.productservice.service.flashsale.cache.FlashSaleCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashSalesReconcileScheduler {

    private final FlashSaleRepository flashSaleRepository;
    private final FlashSaleReservationRepository reservationRepository;
    private final FlashSaleCacheService cacheService;

    @Scheduled(fixedDelay = 300000)
    @SchedulerLock(
            name = "reconcileFlashSales",
            lockAtMostFor = "10m",
            lockAtLeastFor = "10s"
    )
    public void reconcileFlashSales() {

        List<FlashSale> sales =
                flashSaleRepository.findByStatus(FlashSaleStatus.ACTIVE);

        for (FlashSale sale : sales) {

            long reserved =
                    reservationRepository.sumQuantityByFlashSaleIdAndStatusIn(
                            sale.getId(),
                            List.of(
                                    ReservationStatus.PENDING,
                                    ReservationStatus.COMPLETED
                            )
                    );

            int actualStock =
                    sale.getTotalQuantity() - (int) reserved;

            Integer redisStock =
                    cacheService.get(sale.getId());

            if (!Objects.equals(redisStock, actualStock)) {
                cacheService.save(sale.getId(), actualStock);
            }

            log.info(
                    "FLASH_SALE_RECONCILE saleId={} redis={} actual={}",
                    sale.getId(),
                    redisStock,
                    actualStock
            );

        }


    }



    @Scheduled(fixedDelay = 60000)
    @Transactional
    @SchedulerLock(
            name = "updateStatuses",
            lockAtMostFor = "5m"
    )
    public void updateStatuses() {

        log.info("UPDATE STATUSES");

        Instant now = Instant.now();

        List<FlashSale> scheduled =
                flashSaleRepository.findByStatusAndStartsAtBefore(
                        FlashSaleStatus.SCHEDULED,
                        now
                );

        log.info(
                "scheduled -> active size={} now={}",
                scheduled.size(),
                now
        );

        scheduled.forEach(
                sale -> sale.setStatus(FlashSaleStatus.ACTIVE)
        );

        List<FlashSale> active =
                flashSaleRepository.findByStatusAndEndsAtBefore(
                        FlashSaleStatus.ACTIVE,
                        now
                );

        log.info(
                "active -> ended size={} now={}",
                scheduled.size(),
                now
        );

        active.forEach(
                sale -> sale.setStatus(FlashSaleStatus.ENDED)
        );

    }
}
