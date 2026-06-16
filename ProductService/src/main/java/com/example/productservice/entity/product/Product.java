package com.example.productservice.entity.product;

import com.example.productservice.dto.product.ProductDto;
import com.example.productservice.entity.elastic.ProductDocument;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
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

    @Version
    private Long version;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    private String sku;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("5.0")
    @Digits(integer = 1, fraction = 1)
    @Column(name = "average_rating")
    private BigDecimal averageRating = new BigDecimal("0");

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ImageProduct> images;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    private Brand brand;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

    public Product(String name, String description, BigDecimal price, Integer stockQuantity, List<ImageProduct> images, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.availableQuantity = stockQuantity;
        this.images = images;

        for (ImageProduct image : images) {
            image.setProduct(this);
        }

        this.category = category;
    }


    public ProductDocument toDoc() {
        return new ProductDocument(
                id,
                name,
                description,
                price,
                category.getId()
        );
    }

}
