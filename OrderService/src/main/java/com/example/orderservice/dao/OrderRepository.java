package com.example.orderservice.dao;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findAllByUserId(Long userId);
}
