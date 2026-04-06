package com.example.productservice.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
@Data
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Long price;
    private Integer stockQuantity;
    private LocalDate createdAt;


}
