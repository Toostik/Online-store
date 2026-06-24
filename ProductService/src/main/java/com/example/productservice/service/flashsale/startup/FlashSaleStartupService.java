package com.example.productservice.service.flashsale.startup;

import com.example.productservice.dao.flashsale.FlashSaleRepository;
import com.example.productservice.dao.flashsale.FlashSaleReservationRepository;
import com.example.productservice.entity.flashsale.FlashSale;
import com.example.productservice.entity.flashsale.FlashSaleStatus;
import com.example.productservice.entity.flashsale.ReservationStatus;
import com.example.productservice.service.flashsale.cache.FlashSaleCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashSaleStartupService {

    private final FlashSaleRepository flashSaleRepository;
    private final FlashSaleReservationRepository reservationRepository;
    private final FlashSaleCacheService cacheService;

    @EventListener(ApplicationReadyEvent.class)
    public void warmUpFlashSales() {

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

            int stock =
                    sale.getTotalQuantity() - (int) reserved;

            cacheService.save(
                    sale.getId(),
                    stock
            );
            log.info(
                    "FLASH_SALE_CACHE_RESTORED saleId={} stock={}",
                    sale.getId(),
                    stock
            );
        }

    }
}
