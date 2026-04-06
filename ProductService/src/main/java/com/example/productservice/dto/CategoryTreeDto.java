package com.example.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoryTreeDto {

    private Long id;
    private String name;
    private String imagePath;
    private List<CategoryTreeDto> children;

}
