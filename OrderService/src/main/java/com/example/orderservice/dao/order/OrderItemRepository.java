package com.example.orderservice.dao.order;

import com.example.orderservice.entity.order.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {

    @Query("""
            SELECT oi
            FROM OrderItem oi
            JOIN oi.order o
            WHERE o.userId = :userId
            ORDER BY o.createdAt DESC
            """)
    List<OrderItem> findRecentItems(Long userId, Pageable pageable);
}
