package com.example.orderservice.dao.order;

import com.example.orderservice.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Long userId);
    int countOrderByUserId(Long id);

    Integer countOrdersByUserId(Long userId);

}
