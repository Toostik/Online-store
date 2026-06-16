package com.example.productservice.dao.category;

import com.example.productservice.entity.product.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    boolean existsByName(String name);
}
