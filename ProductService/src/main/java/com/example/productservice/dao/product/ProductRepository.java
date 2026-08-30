package com.example.productservice.dao.product;

import com.example.productservice.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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


    @EntityGraph(attributePaths = {
            "images",
            "brand",
            "category"
    })
    @Query("""
                select distinct p
                from Product p
                where exists (
                    select 1
                    from ImageProduct ip
                    where ip.product = p
                )
            """)
    Page<Product> getProductsWithImages(Pageable pageable);

    List<Product> findAllByIdIn(List<Long> ids);

    long countByIdIn(List<Long> ids);

    @Modifying
    @Query("""
                update Product p
                set p.availableQuantity = p.availableQuantity - :quantity,
                    p.reservedQuantity = p.reservedQuantity + :quantity
                where p.id = :id
                  and p.availableQuantity >= :quantity
            """)
    int reserve(
            @Param("id") Long id,
            @Param("quantity") Integer quantity
    );

    @Modifying
    @Query("""
                update Product p
                set p.availableQuantity = p.availableQuantity + :quantity,
                    p.reservedQuantity = p.reservedQuantity - :quantity
                where p.id = :id
            """)
    int release(
            @Param("id") Long id,
            @Param("quantity") Integer quantity
    );

    @Modifying
    @Query("""
                update Product p
                set p.reservedQuantity = p.reservedQuantity - :quantity
                where p.id = :id
            """)
    int commit(
            @Param("id") Long id,
            @Param("quantity") Integer quantity
    );
}
