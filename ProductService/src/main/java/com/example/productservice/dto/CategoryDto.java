package com.example.productservice.dto;

import com.example.productservice.entity.Category;
import com.example.productservice.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private String imagePath;
    private Long parentId;

    public Category toEntity(Category parent){
        return new Category(
                id,
                name,
                imagePath,
                parent
        );
    }
}
