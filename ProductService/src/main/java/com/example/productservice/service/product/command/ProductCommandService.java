package com.example.productservice.service.product.command;

import com.example.productservice.dao.category.CategoryRepository;
import com.example.productservice.dao.product.ProductRepository;
import com.example.productservice.dto.product.ProductDto;
import com.example.productservice.dto.product.request.CreateProductRequest;
import com.example.productservice.dto.product.request.UpdateProductRequest;
import com.example.productservice.entity.product.Category;
import com.example.productservice.entity.product.ImageProduct;
import com.example.productservice.entity.product.Product;
import com.example.productservice.entity.product.mapper.ProductMapper;
import com.example.productservice.exceptions.category.CategoryNotFoundException;
import com.example.productservice.exceptions.product.ProductNotFoundException;
import com.example.productservice.service.product.cache.ProductCacheService;
import com.example.productservice.service.product.event.ProductOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductCommandService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductCacheService productCacheService;
    private final ProductOutboxService productOutboxService;
    private final ProductMapper productMapper;

    public void updateProduct(Long id, UpdateProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if(request.getName() != null)
            product.setName(request.getName());

        if(request.getDescription() != null)
            product.setDescription(request.getDescription());

        boolean priceUpdated = false;

        if(request.getPrice() != null){

            product.setPrice(request.getPrice());

            priceUpdated = true;
        }

        productCacheService.delete(product.getId());

        if(priceUpdated){
            productOutboxService.publishPriceUpdated(product);
        }

    }

    public ProductDto createProduct(CreateProductRequest request) {

        log.info("Creating product: name={}, categoryId={}",
                request.getName(), request.getCategoryId());

        List<String> imagePaths = Optional.ofNullable(request.getImagePaths())
                .orElse(List.of());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found!"));

        List<ImageProduct> images = imagePaths.stream()
                .map(ImageProduct::new)
                .toList();

        Product product;

        if (!images.isEmpty()) {
            product = request.toEntity(images, category);
        } else {
            product = request.toEntityWithoutImages(category);
        }

        product.setCreatedAt(LocalDate.now());

        Product saved = productRepository.save(product);

        productCacheService.save(productMapper.toDto(saved));

        productOutboxService.publishCreated(saved);

        log.info("PRODUCT_CREATED id={}", saved.getId());

        return productMapper.toDto(saved);

// Elastic
//        ProductDocument doc = new ProductDocument();
//        doc.setId(saved.getId());
//        doc.setName(saved.getName());
//        doc.setDescription(saved.getDescription());
//        doc.setPrice(saved.getPrice());
//        doc.setCategoryId(saved.getCategory().getId());
//        productSearchRepository.save(doc);

    }

    public void deleteProduct(Long id){

        Product product = productRepository.findById(id).orElseThrow();

        productRepository.delete(product);

        productCacheService.delete(id);

        productOutboxService.publishDeleted(id);

    }
}
