package com.example.productservice.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoryTreeDto {

    private Long id;
    private String name;
    private String imagePath;
    private List<CategoryTreeDto> children;

}
