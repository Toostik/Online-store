package com.example.productservice.controller.category;

import com.example.productservice.dto.category.CategoryDto;
import com.example.productservice.dto.category.CategoryTreeDto;
import com.example.productservice.dto.category.request.CreateCategoryRequest;
import com.example.productservice.service.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<List<CategoryTreeDto>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    @PostMapping("/create")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequest request){
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

//    @GetMapping("/{id}/products")
//    public ResponseEntity<?> getProductsByCategory(@PathVariable Long id){
//        return ResponseEntity.ok(categoryService.getProductsByCategory(id));
//    }

}
