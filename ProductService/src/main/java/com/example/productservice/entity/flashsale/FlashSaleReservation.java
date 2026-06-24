package com.example.productservice.entity.flashsale;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "flash_sale_reservations",
        indexes = {
                @Index(name = "idx_flash_sale_expires", columnList = "expires_at"),
                @Index(name = "idx_flash_sale_user", columnList = "user_id")
        }
)
public class FlashSaleReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "flash_sale_id", nullable = false)
    private FlashSale flashSale;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "reserved_price",nullable = false, precision = 12, scale = 2)
    private BigDecimal reservedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(name = "order_id", unique = true)
    private Long orderId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "reservation_key", nullable = false, unique = true, updatable = false)
    @Builder.Default
    private UUID reservationKey = UUID.randomUUID();

    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;

}
