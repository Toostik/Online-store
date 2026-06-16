package com.example.cartservice.dao.cart;

import com.example.cartservice.entity.cart.Cart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends CrudRepository<Cart, Long> {
    Optional<Cart> findCartByUserId(Long userId);

    Optional<Cart> findByUserId(Long id);

    boolean existsByUserId(Long userId);
}
