package com.example.cartservice.service;

import com.example.cartservice.dao.CartRepository;
import com.example.cartservice.dto.CartDto;
import com.example.cartservice.dto.CartItemDto;
import com.example.cartservice.entity.Cart;
import com.example.cartservice.entity.CartItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartQueryService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final SecurityService securityService;
    private final CartRepository cartRepository;
    private final ObjectMapper objectMapper;
    @Cacheable(value = "cartDetail", key = "#id", unless = "#result == null")
    public List<CartItemDto> getCartById(Long id) {

        Cart cart = cartRepository.findCartByUserId(id).orElseThrow(
                () -> new RuntimeException("Cart not found")
        );

        return cart.getItems().stream().map(CartItem::toDto).toList();

    }
    public CartDto getCartByCurrentUser() {

        Long userId = securityService.getCurrentUserId();

        String key = "cart:" + userId;

        Object cachedCart = redisTemplate.opsForValue().get(key);

        if(cachedCart == null){
            Cart cartFromDb = cartRepository.findCartByUserId(userId).orElseThrow(
                    () -> new RuntimeException("Cart not found")
            );
            String cartKey = "cart:" + userId;
            redisTemplate.opsForValue().set(cartKey,cartFromDb.toDto(), Duration.ofHours(1));
            return cartFromDb.toDto();
        }

        return objectMapper.convertValue(cachedCart, CartDto.class);
    }
}
