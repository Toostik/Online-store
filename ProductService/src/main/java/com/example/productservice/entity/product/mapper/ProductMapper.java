package com.example.productservice.entity.product.mapper;

import com.example.productservice.dto.product.ProductDto;
import com.example.productservice.entity.product.Brand;
import com.example.productservice.entity.product.Category;
import com.example.productservice.entity.product.ImageProduct;
import com.example.productservice.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "brand", source = "brand")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "stockStatus",
            expression = "java(product.getAvailableQuantity() > 0)")
    @Mapping(target = "imageUrls", source = "images")
    ProductDto toDto(Product product);

    List<ProductDto> toDtoList(List<Product> products);

    default String map(Brand brand) {

        return brand == null
                ? null
                : brand.getName();

    }

    default String map(Category category) {

        return category == null
                ? null
                : category.getName();

    }

    default List<String> map(List<ImageProduct> images) {

        if (images == null) {
            return List.of();
        }

        return images.stream()
                .map(ImageProduct::getImagePath)
                .toList();

    }

}
