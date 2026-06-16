package com.example.productservice.dto.product.request;

import com.example.productservice.entity.product.Category;
import com.example.productservice.entity.product.ImageProduct;
import com.example.productservice.entity.product.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    @NotBlank(message = "The name must be filled")
    private String name;
    private String description;
    @NotNull(message = "The price must be specified")
    private BigDecimal price;
    @NotNull(message = "The value must be 0 or greater")
    private Integer stockQuantity;
    private List<String> imagePaths;
    @NotNull(message = "The category must be selected")
    private Long categoryId;

    public Product toEntity(List<ImageProduct> images, Category category){
        return new Product(
                name,
                description,
                price,
                stockQuantity,
                images,
                category
        );
    }
    public Product toEntityWithoutImages(Category category){
        return new Product(
                name,
                description,
                price,
                stockQuantity,
                List.of(),
                category
        );
    }
}
