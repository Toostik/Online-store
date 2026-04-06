package com.example.productservice.dao;

import com.example.productservice.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    Optional<Product> findProductByName(String name);

    @Query("SELECT p.id, p.price FROM Product p WHERE p.id IN :ids")
    List<Object[]> findPricesByIds(@Param("ids") List<Long> ids);
}
