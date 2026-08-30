package com.example.orderservice.entity.delivery;

import jakarta.persistence.*;
import lombok.*;
import org.example.events.order.DeliveryMethod;

import java.math.BigDecimal;

@Entity
@Table(name = "deliveries")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DeliveryMethod type;

    private BigDecimal price;

    private Integer estimatedDays;

}
