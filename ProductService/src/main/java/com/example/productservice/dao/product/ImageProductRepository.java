package com.example.productservice.dao.product;

import com.example.productservice.entity.product.ImageProduct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageProductRepository extends CrudRepository<ImageProduct, Long> {
}
