package com.example.productservice.dto;

import com.example.productservice.entity.Category;
import com.example.productservice.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private String imagePath;
    private List<CategoryDto> children;
    private List<ProductDto> products;
}
