package com.example.productservice.service;

import com.example.productservice.dao.CategoryRepository;
import com.example.productservice.dto.CategoryDto;
import com.example.productservice.dto.CategoryTreeDto;
import com.example.productservice.entity.Category;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryTreeDto> getAllCategories() {
        List<Category> categories = (List<Category>) categoryRepository.findAll();

        if(categories.isEmpty()){
            throw new RuntimeException("Product list is empty");
        }

        List<CategoryTreeDto> categoryTreeDtoList = categories.stream()
                .filter(category -> category.getParent() == null)
                .map(Category::toTreeDto)
                .toList();

        return categoryTreeDtoList;
    }

//    public List<?> getProductsByCategory(Long id) {
//        Category category = categoryRepository.findById(id).orElseThrow(
//                () -> new RuntimeException("Category doesn't exist")
//        );
//        CategoryDto categoryDto = category.toDto();
//        if(categoryDto.getProducts().isEmpty()){
//            return categoryDto.getChildren().stream()
//                    .map(CategoryDto::getName)
//                    .toList();
//        }else if(categoryDto.getChildren().isEmpty()){
//            return categoryDto.getProducts();
//        } else {
//            throw new RuntimeException("Category doesn't have children and products");
//        }
//
//    }
    @Cacheable(value = "category", key = "#id", unless = "#result == null")
    public CategoryDto getCategoryById(Long id){

        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Category not found!")
        );

        return category.toDto();
    }
}
