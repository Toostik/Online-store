package com.example.productservice.entity.reserve;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reserved_products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Integer quantity;

}
