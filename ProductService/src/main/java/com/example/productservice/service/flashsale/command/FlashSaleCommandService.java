package com.example.productservice.service.flashsale.command;

import com.example.productservice.dao.flashsale.FlashSaleRepository;
import com.example.productservice.dao.flashsale.FlashSaleReservationRepository;
import com.example.productservice.dao.product.ProductRepository;
import com.example.productservice.dto.flashsale.CreateFlashSaleOrderRequest;
import com.example.productservice.dto.flashsale.FlashSaleReservationResponse;
import com.example.productservice.dto.flashsale.ReserveFlashSaleRequest;
import com.example.productservice.dto.flashsale.request.FlashSaleCreateRequest;
import com.example.productservice.entity.flashsale.FlashSale;
import com.example.productservice.entity.flashsale.FlashSaleReservation;
import com.example.productservice.entity.flashsale.FlashSaleStatus;
import com.example.productservice.entity.flashsale.ReservationStatus;
import com.example.productservice.entity.product.Product;
import com.example.productservice.exceptions.flashsale.*;
import com.example.productservice.exceptions.product.ProductNotFoundException;
import com.example.productservice.service.flashsale.builder.FlashSaleReservationBuilder;
import com.example.productservice.service.flashsale.cache.FlashSaleCacheService;
import com.example.productservice.service.flashsale.cache.FlashSaleReserveCacheService;
import com.example.productservice.service.flashsale.event.FlashSaleOutboxService;
import com.example.productservice.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.flashsale.FlashSaleReservationAndCheckoutEvent;
import org.example.events.order.AddressDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FlashSaleCommandService {

    private final FlashSaleReserveCacheService cacheService;
    private final FlashSaleRepository flashSaleRepository;
    private final SecurityService securityService;
    private final FlashSaleReservationBuilder flashSaleReservationBuilder;
    private final FlashSaleReservationRepository flashSaleReservationRepository;
    private final FlashSaleOutboxService flashSaleOutboxService;
    private final ProductRepository productRepository;
    private final FlashSaleCacheService flashSaleCacheService;

    public FlashSaleReservationResponse reserve(
            Long id,
            ReserveFlashSaleRequest request
    ) {

        if (request.quantity() <= 0) {
            throw new FlashSaleReserveException(
                    "The reservation number is incorrect -> " + request.quantity()
            );
        }

        FlashSale flashSale = flashSaleRepository.findById(id)
                .orElseThrow(
                        () -> new FlashSaleNotFoundException("Flash sale not found")
                );

        if (flashSale.getStatus() != FlashSaleStatus.ACTIVE) {
            throw new FlashNotActiveException("Flash sale is not active");
        }

        if (flashSale.getEndsAt().isBefore(Instant.now())) {
            throw new FlashSaleReserveException("Flash sale ended");
        }

        boolean reserved =
                cacheService.isCacheReserved(
                        flashSale.getId(),
                        request.quantity()
                );

        if (!reserved) {
            throw new FlashSaleSoldOutException();
        }

        Long userId = securityService.getCurrentUserId();

        FlashSaleReservation flashSaleReservation =
                flashSaleReservationBuilder.create(
                        flashSale,
                        request.quantity(),
                        userId
                );

        flashSaleReservationRepository.save(flashSaleReservation);

        log.info(
                "FLASH_SALE_RESERVED saleId={} userId={} quantity={} reservationKey={}",
                flashSale.getId(),
                userId,
                request.quantity(),
                flashSaleReservation.getReservationKey()
        );

        return new FlashSaleReservationResponse(
                flashSaleReservation.getReservationKey(),
                flashSaleReservation.getExpiresAt(),
                flashSaleReservation.getStatus()
        );
    }

    public void createFlashSaleOrder(String reservationKey, CreateFlashSaleOrderRequest request) {

        FlashSaleReservation reservation = flashSaleReservationRepository.findByReservationKey(UUID.fromString(reservationKey))
                .orElseThrow(
                        () -> new FlashSaleReserveException("Reservation not found")
                );

        if(!reservation.getStatus().equals(ReservationStatus.PENDING)){
            throw new FlashSaleReserveException("Reservation status is incorrect");
        }

        FlashSaleReservationAndCheckoutEvent event = new FlashSaleReservationAndCheckoutEvent(
                UUID.randomUUID(),
                reservation.getReservationKey(),
                reservation.getFlashSale().getId(),
                reservation.getUserId(),
                reservation.getFlashSale().getProduct().getId(),
                reservation.getQuantity(),
                reservation.getFlashSale().getDiscountedPrice(),
                new AddressDto(
                        request.country(),
                        request.city(),
                        request.address(),
                        request.apartment(),
                        request.postalCode()
                ),
                request.deliveryMethod()
        );

        flashSaleOutboxService.publishCreated(event);

        log.info(
                "FLASH_SALE_CHECKOUT reservationKey={}",
                reservation.getReservationKey()
        );
    }

    public void createFlashSale(Long productId, FlashSaleCreateRequest request) {

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException(productId)
        );

        if (request.quantity() > product.getAvailableQuantity()) {
            throw new FlashSaleCreateException("Quantity is less than available");
        }

        if(request.discountedPrice().compareTo(product.getPrice()) >= 0){
            throw new FlashSaleCreateException("Discounted price is incorrect");
        }

        if (request.startsAt().isAfter(request.endsAt())){
            throw new FlashSaleCreateException("Starts time is incorrect");
        }

        if (request.startsAt().isBefore(Instant.now())){
            throw new FlashSaleCreateException("Ends time is incorrect");
        }

        FlashSale flashSale = FlashSale.builder()
                .product(product)
                .discountedPrice(request.discountedPrice())
                .originalPrice(product.getPrice())
                .totalQuantity(request.quantity())
                .startsAt(request.startsAt())
                .endsAt(request.endsAt())
                .build();

        FlashSale saved = flashSaleRepository.save(flashSale);

        flashSaleCacheService.save(saved.getId(), saved.getTotalQuantity());

        log.info(
                "FLASH_SALE_CREATED saleId={} productId={} quantity={}",
                saved.getId(),
                productId,
                saved.getTotalQuantity()
        );
    }
}
