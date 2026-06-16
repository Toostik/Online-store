package com.example.cartservice.dao.cart;

import com.example.cartservice.entity.cart.CartItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemsRepository extends CrudRepository<CartItem,Long> {
    List<CartItem> findAllByCartId(Long cartId);

    @Modifying
    @Query("""
UPDATE CartItem ci
SET ci.quantity = ci.quantity + :qty
WHERE ci.cart.id = :cartId AND ci.productId = :productId
""")
    int incrementQuantity(Long cartId, Long productId, int qty);

    Optional<CartItem> findByIdAndCartUserId(
            Long itemId,
            Long userId
    );

}
