package com.example.productservice.dao.flashsale;


import com.example.productservice.entity.flashsale.FlashSaleReservation;
import com.example.productservice.entity.enums.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FlashSaleReservationRepository extends JpaRepository<FlashSaleReservation, Long> {

    Optional<FlashSaleReservation> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    @EntityGraph(attributePaths = {
            "flashSale",
            "flashSale.product"
    })
    Optional<FlashSaleReservation> findByReservationKey(UUID reservationKey);

    List<FlashSaleReservation> findByStatusAndExpiresAtBefore(
            ReservationStatus status,
            Instant expiresAt
    );

    boolean existsByReservationKey(UUID uuid);

    @Query("""
            select coalesce(sum(r.quantity), 0)
            from FlashSaleReservation r
            where r.flashSale.id = :flashSaleId
            and r.status in :statuses
            """)
    Integer sumQuantityByFlashSaleIdAndStatusIn(
            @Param("flashSaleId") Long flashSaleId,
            @Param("statuses") List<ReservationStatus> statuses
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                select r
                from FlashSaleReservation r
                where r.reservationKey = :reservationKey
            """)
    Optional<FlashSaleReservation> findByReservationKeyForUpdate(
            @Param("reservationKey") UUID reservationKey
    );

}
