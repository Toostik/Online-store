package com.example.productservice.dao.product;

import com.example.productservice.entity.product.ImageProduct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageProductRepository extends CrudRepository<ImageProduct, Long> {
    Optional<ImageProduct> findByImagePath(String imagePath);
}
