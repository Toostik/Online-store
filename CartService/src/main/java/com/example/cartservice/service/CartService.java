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
import com.example.cartservice.exceptions.CartHasNotProductsException;
import com.example.cartservice.exceptions.NotEnoughException;
import com.example.cartservice.exceptions.NotExistException;
import com.example.cartservice.kafka.KafkaProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private final SecurityService securityService;
    private final CartQueryService cartQueryService;

    public void createCart(List<CartItemDto> items) {

        Long userId = securityService.getCurrentUserId();

        if(cartRepository.existsByUserId(userId)){
            throw new RuntimeException("Cart exists");
        }

        Cart cart = new Cart();

        List<Long> ids = items.stream().map(CartItemDto::getProductId).toList();

        Map<Long, BigDecimal> prices = productService.getPrices(ids);

        if(prices == null || prices.isEmpty()){
            throw new RuntimeException("Prices by product service is null");
        }

        List<CartItem> cartItems = items.stream()
                .map(cartItemDto ->
                        cartItemDto.toEntity(cart, prices.get(cartItemDto.getProductId()))
                )
                .toList();

        cart.setUserId(userId);
        cart.setItems(cartItems);
        cartRepository.save(cart);
    }

    public void addToCart(List<CartItemDto> items){

        Long userId = securityService.getCurrentUserId();

        Cart cart = cartRepository.findCartByUserId(userId).orElseThrow(
                () -> new RuntimeException("Cart doesn't exist")
        );

        List<Long> ids = items.stream().map(CartItemDto::getProductId).toList();

        Map<Long, BigDecimal> prices = productService.getPrices(ids);

        if(prices == null || prices.isEmpty()){
            throw new RuntimeException("Prices by product service is null");
        }

        List<CartItem> cartItems = items.stream()
                .map(cartItemDto ->
                        cartItemDto.toEntity(cart, prices.get(cartItemDto.getProductId()))
                )
                .toList();

        for(CartItem item: cartItems){
            cart.getItems().add(item);
        }

        cartRepository.save(cart);
    }

    public void createOrder() {
        CartDto cartDto = cartQueryService.getCartByCurrentUser();

        if(cartDto == null){
            throw new NotExistException("Cart doesn't exist");
        }

        if(cartDto.getItems() == null || cartDto.getItems().isEmpty()){
            throw new CartHasNotProductsException("Cart has not products");
        }

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
                throw new NotExistException("Product " + productId + " not found");
            }

            if (!availability.isEnoughStock()) {
                throw new NotEnoughException(productId);
            }
        }

        kafkaProducer.sendMessage(CART_TOPIC, cartDto);

        clearCart();
    }

    public void clearCart() {

        Long userId = securityService.getCurrentUserId();

        String key = "cart:" + userId;

        redisTemplate.delete(key);

        Cart cart = cartRepository.findCartByUserId(userId).orElseThrow(
                () -> new NotExistException("Cart not found!")
        );

        cart.setItems(List.of());
        cartRepository.save(cart);
    }

    public void deleteCart() {
        Long userId = securityService.getCurrentUserId();

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

}
