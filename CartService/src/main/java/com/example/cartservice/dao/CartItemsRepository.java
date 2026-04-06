package com.example.cartservice.dao;

import com.example.cartservice.entity.CartItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemsRepository extends CrudRepository<CartItem,Long> {
}
