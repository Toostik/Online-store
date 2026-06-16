package com.example.productservice.service.category;

import com.example.productservice.dao.category.CategoryRepository;
import com.example.productservice.dto.category.CategoryDto;
import com.example.productservice.dto.category.CategoryTreeDto;
import com.example.productservice.dto.category.request.CreateCategoryRequest;
import com.example.productservice.entity.product.Category;
import com.example.productservice.exceptions.category.CategoryFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public CategoryDto createCategory(CreateCategoryRequest request) {

        if(categoryRepository.existsByName(request.getName())){
            throw new CategoryFoundException("Category already exists");
        }

        Category category = new Category();

        if(request.getParentId() != null){
            category.setParent(categoryRepository.findById(request.getParentId()).orElseThrow(
                    () -> new CategoryFoundException("Category not found")
            ));
        }

        category.setName(request.getName());

        Category saved = categoryRepository.save(category);

        return saved.toDto();
    }
}
