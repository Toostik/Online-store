package com.example.productservice.controller;

import com.example.productservice.dto.CategoryDto;
import com.example.productservice.dto.CategoryTreeDto;
import com.example.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryTreeDto>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

//    @GetMapping("/{id}/products")
//    public ResponseEntity<?> getProductsByCategory(@PathVariable Long id){
//        return ResponseEntity.ok(categoryService.getProductsByCategory(id));
//    }

}
