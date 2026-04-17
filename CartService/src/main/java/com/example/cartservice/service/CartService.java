package com.example.cartservice.service;

import com.example.cartservice.dao.CartItemsRepository;
import com.example.cartservice.dao.CartRepository;
import com.example.cartservice.dto.CartDto;
import com.example.cartservice.dto.CartItemDto;
import com.example.cartservice.dto.request.CheckProductRequest;
import com.example.cartservice.dto.request.CheckProductResponse;
import com.example.cartservice.dto.request.ProductAvailability;
import com.example.cartservice.entity.Cart;
import com.example.cartservice.entity.CartItem;
import com.example.cartservice.kafka.KafkaProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final String CART_TOPIC = "cart-checkout";
    private final KafkaProducer kafkaProducer;
    private final ProductService productService;
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private final ObjectMapper objectMapper;



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

        Map<Long, BigDecimal> prices = productService.getPrices(ids);

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

        Map<Long, BigDecimal> prices = productService.getPrices(ids);

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

    public void createOrder() {
        CartDto cartDto = getCartByCurrentUser();
        List<CartItemDto> items = cartDto.getItems();

        Map<Long, Integer> products =  items.stream()
                        .collect(Collectors.toMap(
                                CartItemDto::getProductId,
                                CartItemDto::getQuantity
                        ));

        CheckProductRequest request = new CheckProductRequest();
        request.setProducts(products);
        CheckProductResponse response = productService.checkAvailability(request);

        for (Map.Entry<Long, ProductAvailability> entry : response.getProductAvailability().entrySet()) {

            Long productId = entry.getKey();
            ProductAvailability availability = entry.getValue();

            if (!availability.isExists()) {
                throw new RuntimeException("Product " + productId + " not found");
            }

            if (!availability.isEnoughStock()) {
                throw new RuntimeException("Not enough stock for product " + productId);
            }
        }

        kafkaProducer.sendMessage(CART_TOPIC, cartDto);

        clearCart(cartDto.getId());
    }

    private void clearCart(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());
        String key = "cart:" + userId;
        redisTemplate.delete(key);

        Cart cart = cartRepository.findCartByUserId(userId).orElseThrow(
                () -> new RuntimeException("Cart not found!")
        );
        cart.setItems(null);
        cartRepository.save(cart);
    }

    public void deleteCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());

        String key = "cart:" + userId;

        redisTemplate.delete(key);

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

    public CartDto getCartByCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());

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
