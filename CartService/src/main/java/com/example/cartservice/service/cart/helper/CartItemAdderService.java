package com.example.cartservice.service.cart.helper;

import com.example.cartservice.dao.cart.CartItemsRepository;
import com.example.cartservice.dto.cart.CartItemDto;
import com.example.cartservice.entity.cart.Cart;
import com.example.cartservice.entity.cart.CartItem;
import com.example.cartservice.exceptions.product.PricesEmptyException;
import com.example.cartservice.service.integration.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartItemAdderService {

    private final CartItemsRepository cartItemsRepository;
    private final ProductService productService;

    public void addItems(Cart cart, List<CartItemDto> items) {

        List<Long> ids = items.stream()
                .map(CartItemDto::getProductId)
                .toList();

        Map<Long, BigDecimal> prices = productService.getPrices(ids);

        if (prices == null || prices.isEmpty()) {
            throw new PricesEmptyException();
        }

        for (CartItemDto dto : items) {

            int updated = cartItemsRepository.incrementQuantity(
                    cart.getId(),
                    dto.getProductId(),
                    dto.getQuantity()
            );

            if (updated == 0) {

                CartItem item = dto.toEntity(
                        cart,
                        prices.get(dto.getProductId())
                );

                cartItemsRepository.save(item);
            }
        }

    }

}