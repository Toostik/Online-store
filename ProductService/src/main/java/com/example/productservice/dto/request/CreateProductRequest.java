package com.example.productservice.dto.request;

import com.example.productservice.entity.Category;
import com.example.productservice.entity.ImageProduct;
import com.example.productservice.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private List<String> imagePaths;
    private Category category;

    public Product toEntity(List<ImageProduct> images){
        return new Product(
                name,
                description,
                price,
                stockQuantity,
                images,
                category
        );
    }
}
