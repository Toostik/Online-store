package com.example.productservice.dao;

import com.example.productservice.entity.ImageProduct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageProductRepository extends CrudRepository<ImageProduct, Long> {
}
