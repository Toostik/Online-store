package com.example.productservice.service.inventory.command;

import com.example.productservice.dao.event.ProcessedEventRepository;
import com.example.productservice.dao.product.ProductRepository;
import com.example.productservice.dao.reserve.OrderReservationRepository;
import com.example.productservice.entity.event.ProcessedEvent;
import com.example.productservice.entity.product.Product;
import com.example.productservice.entity.reserve.OrderReservation;
import com.example.productservice.entity.reserve.ReservedProduct;
import com.example.productservice.exceptions.product.InsufficientStockException;
import com.example.productservice.exceptions.product.ProductNotFoundException;
import com.example.productservice.service.inventory.event.InventoryOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.inventory.*;
import org.example.events.order.OrderItemEvent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryCommandService {

    private final ProductRepository productRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final InventoryOutboxService inventoryOutboxService;
    private final OrderReservationRepository reservationRepository;

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

    public void reserve(
            InventoryReserveRequestedEvent event
    ) {

        if (!markProcessed(event.eventId())) {

            log.warn(
                    "DUPLICATE_INVENTORY_RESERVE_REQUEST_SKIPPED eventId={}",
                    event.eventId()
            );

            return;
        }

        Map<Long, Integer> products =
                event.items()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        OrderItemEvent::productId,
                                        OrderItemEvent::stockQuantity
                                )
                        );

        products.forEach((id, qty) -> {

            Product product = productRepository.findById(id)
                    .orElseThrow(
                            () -> new ProductNotFoundException(id)
                    );

            if (product.getAvailableQuantity() < qty) {

                throw new InsufficientStockException(
                        "Stock not enough"
                );

            }

            product.setAvailableQuantity(
                    product.getAvailableQuantity() - qty
            );

            product.setReservedQuantity(
                    product.getReservedQuantity() + qty
            );

        });

        OrderReservation reservation =
                OrderReservation.builder()
                        .orderId(event.orderId())
                        .products(
                                event.items()
                                        .stream()
                                        .map(item ->
                                                ReservedProduct.builder()
                                                        .productId(item.productId())
                                                        .quantity(item.stockQuantity())
                                                        .build()
                                        )
                                        .toList()
                        )
                        .build();

        reservationRepository.save(reservation);

        InventoryReservedEvent reservedEvent =
                new InventoryReservedEvent(
                        UUID.randomUUID().toString(),
                        event.correlationId(),
                        event.orderId(),
                        event.items()
                );

        inventoryOutboxService.publishInventoryReserved(
                reservedEvent
        );

        log.info(
                "INVENTORY_RESERVED orderId={} eventId={}",
                event.orderId(),
                event.eventId()
        );

    }

    public void releaseInventory(
            InventoryReleaseRequestedEvent event
    ) {

        if (!markProcessed(event.eventId())) {

            log.warn(
                    "DUPLICATE_INVENTORY_RELEASE_SKIPPED eventId={}",
                    event.eventId()
            );

            return;
        }

        OrderReservation reservation =
                reservationRepository.findByOrderId(
                        event.orderId()
                ).orElseThrow();

        for (ReservedProduct item : reservation.getProducts()) {

            Product product =
                    productRepository.findById(
                            item.getProductId()
                    ).orElseThrow();

            product.setReservedQuantity(
                    product.getReservedQuantity()
                            - item.getQuantity()
            );

            product.setAvailableQuantity(
                    product.getAvailableQuantity()
                            + item.getQuantity()
            );

        }

        reservationRepository.delete(
                reservation
        );

        InventoryReleasedEvent releasedEvent =
                new InventoryReleasedEvent(
                        UUID.randomUUID().toString(),
                        event.correlationId(),
                        event.orderId()
                );

        inventoryOutboxService.publishInventoryReleased(
                releasedEvent
        );



    }

    public void commitInventory(
            InventoryCommitRequestedEvent event
    ) {

        if (!markProcessed(event.eventId())) {

            log.warn(
                    "DUPLICATE_INVENTORY_COMMIT_REQUEST_SKIPPED eventId={}",
                    event.eventId()
            );

            return;
        }

        OrderReservation reservation =
                reservationRepository.findByOrderId(
                        event.orderId()
                ).orElseThrow();

        for (ReservedProduct item : reservation.getProducts()) {

            Product product =
                    productRepository.findById(
                            item.getProductId()
                    ).orElseThrow();

            product.setReservedQuantity(
                    product.getReservedQuantity()
                            - item.getQuantity()
            );

        }

        reservationRepository.delete(
                reservation
        );

        InventoryCommittedEvent committedEvent =
                new InventoryCommittedEvent(
                        UUID.randomUUID().toString(),
                        event.correlationId(),
                        event.orderId(),
                        event.userId(),
                        event.amount()
                );

        inventoryOutboxService.publishCommitted(
                committedEvent
        );

        log.info(
                "INVENTORY_COMMITTED orderId={}",
                event.orderId()
        );

    }

}
