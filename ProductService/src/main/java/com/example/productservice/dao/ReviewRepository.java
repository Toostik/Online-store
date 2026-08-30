package com.example.productservice.dao;

import com.example.productservice.entity.product.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("""
    select r.product.id, count(r)
    from Review r
    where r.product.id in :ids
    group by r.product.id
""")
    List<Object[]> countReviewsByProductIds(
            @Param("ids") List<Long> ids
    );

}
