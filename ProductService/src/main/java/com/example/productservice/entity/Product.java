package com.example.productservice.entity;

import com.example.productservice.dto.CategoryDto;
import com.example.productservice.dto.ProductDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "stock_quantity")
    private Integer stockQuantity;
    @Column(name = "created_at")
    private LocalDate createdAt;

    @OneToMany(mappedBy = "product")
    private List<ImageProduct> images;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public ProductDto toDto() {
        return new ProductDto(
                id,
                name,
                description,
                price,
                stockQuantity,
                createdAt
        );
    }
}
