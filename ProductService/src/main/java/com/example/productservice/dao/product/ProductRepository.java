package com.example.productservice.dao.product;

import com.example.productservice.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findProductByName(String name);

    @Query("SELECT p.id, p.price FROM Product p WHERE p.id IN :ids")
    List<Object[]> findPricesByIds(@Param("ids") List<Long> ids);

    boolean existsById(Long id);

    @Query("SELECT p.price FROM Product p WHERE p.id IN :id")
    BigDecimal getPriceById(Long id);

    @Query("SELECT distinct p FROM Product p join p.images ip where ip.imagePath is not null")
    List<Product> getProductWithImage(Pageable pageable);

    @Query("""
       SELECT p
       FROM Product p
       WHERE EXISTS (
           SELECT 1
           FROM ImageProduct ip
           WHERE ip.product = p
       )
       """)
    Page<Product> getProductsWithImages(Pageable pageable);

    List<Product> findAllByIdIn(List<Long> ids);

    long countByIdIn(List<Long> ids);
}
