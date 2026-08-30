package com.example.orderservice.dao.order;

import com.example.orderservice.entity.enums.OrderStatus;
import com.example.orderservice.entity.order.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {
            "items",
            "address",
            "delivery"
    })
    List<Order> findAllByUserId(Long userId);

    int countOrderByUserId(Long id);

    Integer countOrdersByUserId(Long userId);

    @Modifying
    @Query("""
    update Order o
    set o.orderStatus = :to
    where o.id = :id and o.orderStatus = :from
    """)
    int changeStatus(Long id, OrderStatus from, OrderStatus to);

}
