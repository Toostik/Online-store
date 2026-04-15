package com.example.cartservice.service;

import com.example.cartservice.dao.CartItemsRepository;
import com.example.cartservice.dao.CartRepository;
import com.example.cartservice.dto.CartItemDto;
import com.example.cartservice.entity.Cart;
import com.example.cartservice.entity.CartItem;
import com.example.cartservice.kafka.KafkaProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final String CART_TOPIC = "cart-checkout";
    private final KafkaProducer kafkaProducer;
    private final PriceService priceService;



    @Cacheable(value = "cartDetail", key = "#id", unless = "#result == null")
    public List<CartItemDto> getCart(Long id) {

            Cart cart = cartRepository.findCartByUserId(id).orElseThrow(
                    () -> new RuntimeException("Cart not found")
            );

            return cart.getItems().stream().map(CartItem::toDto).toList();

    }

    public void createCart(Long userId, List<CartItemDto> items) {

        if(cartRepository.existsByUserId(userId)){
            throw new RuntimeException("Cart exists");
        }

        Cart cart = new Cart();

        List<Long> ids = items.stream().map(CartItemDto::getProductId).toList();

        Map<Long, BigDecimal> prices = priceService.getPrices(ids);

        List<CartItem> cartItems = items.stream()
                .map(cartItemDto ->
                        cartItemDto.toEntity(cart, prices != null ? prices.get(cartItemDto.getProductId()) : null)
                )
                .toList();

        cart.setUserId(userId);
        cart.setItems(cartItems);
        cartRepository.save(cart);

    }

    public void addToCart(Long userId, List<CartItemDto> items){

        Cart cart = cartRepository.findCartByUserId(userId).orElseThrow(
                () -> new RuntimeException("Cart doesn't exist")
        );

        List<Long> ids = items.stream().map(CartItemDto::getProductId).toList();

        Map<Long, BigDecimal> prices = priceService.getPrices(ids);

        List<CartItem> cartItems = items.stream()
                .map(cartItemDto ->
                        cartItemDto.toEntity(cart, prices != null ? prices.get(cartItemDto.getProductId()) : null)
                )
                .toList();

        for(CartItem item: cartItems){
            cart.getItems().add(item);
        }

        cartRepository.save(cart);
    }

    public void createOrder(Long id) {
        Cart cart = cartRepository.findCartByUserId(id).orElseThrow(() ->
                new RuntimeException("Cart doesn't exist"));

        kafkaProducer.sendMessage(CART_TOPIC, cart.toDto());

        cart.getItems().clear();

        cartRepository.save(cart);
    }

    public void deleteCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());

        Cart cart = cartRepository.findCartByUserId(userId).orElseThrow(
                () -> new RuntimeException("Cart not found!")
        );

        cartRepository.delete(cart);
    }


    public void deleteCartItem(Long id) {
        CartItem cartItem = cartItemsRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Cart item not found!")
        );
        cartItemsRepository.delete(cartItem);
    }
}
