package com.example.cartservice.service;

import com.example.cartservice.dao.CartItemsRepository;
import com.example.cartservice.dao.CartRepository;
import com.example.cartservice.dto.CartItemDto;
import com.example.cartservice.entity.Cart;
import com.example.cartservice.entity.CartItem;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;

    @Autowired
    @Qualifier("userServiceWebClient")
    private final WebClient userServiceWebClient;

    @Autowired
    @Qualifier("productServiceWebClient")
    private final WebClient productServiceWebClient;

    private void isUserExistException(Long id){
        Boolean isUserExist = userServiceWebClient.get()
                .uri("/api/users/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .map(response -> true)
                .onErrorReturn(false)
                .block();
        if(!isUserExist){
            throw new RuntimeException("User not found");
        }
    }

    public List<CartItemDto> getCart(Long id) {

            Cart cart = cartRepository.findCartByUserId(id).orElseThrow(
                    () -> new RuntimeException("Cart not found")
            );
            return cart.getItems().stream().map(CartItem::toDto).toList();

    }

    public void createCart(Long userId, List<CartItemDto> items) {
        List<Long> ids = items.stream().map(CartItemDto::getProductId).toList();

        Map<Long, Long> prices = productServiceWebClient.post()
                .uri("/api/products/prices")
                .bodyValue(ids)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Long, Long>>() {
                }).block();


        Cart cart = cartRepository.findByUserId(userId).orElse(new Cart());


        List<CartItem> cartItems = items.stream()
                .map(cartItemDto ->
                    cartItemDto.toEntity(cart, prices != null ? prices.get(cartItemDto.getProductId()) : null)
                )
                .toList();


        if(cart.getId()==null){
            cart.setUserId(userId);
            cartRepository.save(cart);
        } else {
            for(CartItem item: cartItems){
                cart.getItems().add(item);
            }
        }

        cartItemsRepository.saveAll(cartItems);
        cartRepository.save(cart);

        System.out.println("Cart is created or updated");
    }
}
