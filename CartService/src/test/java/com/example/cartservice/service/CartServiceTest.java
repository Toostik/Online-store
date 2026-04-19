package com.example.cartservice.service;

import com.example.cartservice.dao.CartRepository;
import com.example.cartservice.dto.CartDto;
import com.example.cartservice.dto.CartItemDto;
import com.example.cartservice.dto.request.CheckProductResponse;
import com.example.cartservice.dto.request.ProductAvailability;
import com.example.cartservice.entity.Cart;
import com.example.cartservice.entity.CartItem;
import com.example.cartservice.exceptions.CartHasNotProductsException;
import com.example.cartservice.exceptions.NotEnoughException;
import com.example.cartservice.exceptions.NotExistException;
import com.example.cartservice.kafka.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private KafkaProducer kafkaProducer;
    @Mock
    private ProductService productService;
    @Mock
    private SecurityService securityService;
    @Mock
    private CartQueryService cartQueryService;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOps;
    @InjectMocks
    private CartService cartService;

    @Test
    void createCart_shouldSaveInRepository() {
        Long userId = 1L;

        when(securityService.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.existsByUserId(userId)).thenReturn(false);

        List<CartItemDto> items = new ArrayList<>();
        Long productId1 = 1L;
        Long productId2 = 2L;
        items.add(new CartItemDto(1L, 10, productId1));
        items.add(new CartItemDto(2L, 5, productId2));

        Map<Long, BigDecimal> prices = new HashMap<>();
        BigDecimal price1 = new BigDecimal("1000");
        BigDecimal price2 = new BigDecimal("2000");
        prices.put(productId1, price1);
        prices.put(productId2, price2);

        when(productService.getPrices(anyList())).thenReturn(prices);

        cartService.createCart(items);

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);

        verify(productService).getPrices(anyList());
        verify(cartRepository).save(captor.capture());

        Cart saved = captor.getValue();
        assertEquals(userId, saved.getUserId());
        assertEquals(items.size(), saved.getItems().size());
        assertThat(saved.getItems()).extracting(CartItem::getProductId).containsExactlyInAnyOrder(productId1, productId2);
        assertThat(saved.getItems()).extracting(CartItem::getPriceAtAddTime).containsExactlyInAnyOrder(price1, price2);
    }

    @Test
    void createCart_shouldReturnException_whenCartExists() {
        Long userId = 1L;

        when(securityService.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.existsByUserId(userId)).thenReturn(true);

        List<CartItemDto> items = new ArrayList<>();
        Long productId1 = 1L;
        Long productId2 = 2L;
        items.add(new CartItemDto(1L, 10, productId1));
        items.add(new CartItemDto(2L, 5, productId2));

        RuntimeException rx = assertThrows(RuntimeException.class, () ->
                cartService.createCart(items));

        assertEquals("Cart exists", rx.getMessage());

        verifyNoInteractions(productService);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void createCart_shouldReturnException_whenPricesIsEmpty() {
        Long userId = 1L;

        when(securityService.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.existsByUserId(userId)).thenReturn(false);

        List<CartItemDto> items = new ArrayList<>();
        Long productId1 = 1L;
        Long productId2 = 2L;
        items.add(new CartItemDto(1L, 10, productId1));
        items.add(new CartItemDto(2L, 5, productId2));

        when(productService.getPrices(anyList())).thenReturn(new HashMap<>());

        RuntimeException rx = assertThrows(RuntimeException.class, () ->
                cartService.createCart(items));

        assertEquals("Prices by product service is null", rx.getMessage());

        verify(productService).getPrices(anyList());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addToCart_shouldUpdateCart() {
        Long userId = 1L;

        when(securityService.getCurrentUserId()).thenReturn(userId);

        Cart cart = new Cart();
        List<CartItem> items = new ArrayList<>();
        Long productId1 = 1L;
        Long productId2 = 2L;
        Integer quantity1 = 7;
        Integer quantity2 = 3;
        BigDecimal price1 = new BigDecimal("1000");
        BigDecimal price2 = new BigDecimal("2000");
        items.add(new CartItem(quantity1, productId1, price1, cart));
        items.add(new CartItem(quantity2, productId2, price2, cart));
        cart.setItems(items);
        cart.setId(1L);
        cart.setUserId(userId);

        when(cartRepository.findCartByUserId(userId)).thenReturn(Optional.of(cart));

        List<CartItemDto> itemsDto = new ArrayList<>();
        Long productId3 = 3L;
        Long productId4 = 4L;
        Integer quantity3 = 10;
        Integer quantity4 = 5;
        itemsDto.add(new CartItemDto(3L, quantity3, productId3));
        itemsDto.add(new CartItemDto(4L, quantity4, productId4));

        Map<Long, BigDecimal> prices = new HashMap<>();
        BigDecimal price3 = new BigDecimal("1500");
        BigDecimal price4 = new BigDecimal("2300");
        prices.put(productId3, price3);
        prices.put(productId4, price4);

        when(productService.getPrices(anyList())).thenReturn(prices);

        cartService.addToCart(itemsDto);

        verify(cartRepository).findCartByUserId(userId);
        verify(productService).getPrices(anyList());

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);

        verify(cartRepository).save(captor.capture());

        Cart saved = captor.getValue();


        assertThat(saved.getItems()).extracting(CartItem::getProductId).containsExactlyInAnyOrder(productId1, productId2, productId3, productId4);
        assertThat(saved.getItems()).extracting(CartItem::getQuantity).containsExactlyInAnyOrder(quantity1, quantity2, quantity3, quantity4);
        assertThat(saved.getItems()).extracting(CartItem::getPriceAtAddTime).containsExactlyInAnyOrder(price1, price2, price3, price4);
    }

    @Test
    void addToCart_shouldReturnException_whenCartNotExists() {
        Long userId = 1L;

        when(securityService.getCurrentUserId()).thenReturn(userId);

        when(cartRepository.findCartByUserId(userId)).thenReturn(Optional.empty());

        List<CartItemDto> itemsDto = new ArrayList<>();
        Long productId3 = 3L;
        Long productId4 = 4L;
        itemsDto.add(new CartItemDto(3L, 10, productId3));
        itemsDto.add(new CartItemDto(4L, 5, productId4));

        RuntimeException rx = assertThrows(RuntimeException.class,
                () -> cartService.addToCart(itemsDto));

        assertEquals("Cart doesn't exist", rx.getMessage());

        verify(cartRepository).findCartByUserId(userId);
        verify(cartRepository, never()).save(any());
        verifyNoInteractions(productService);
    }

    @Test
    void addToCart_shouldReturnException_whenPricesIsEmpty() {
        Long userId = 1L;

        when(securityService.getCurrentUserId()).thenReturn(userId);

        Cart cart = new Cart();
        List<CartItem> items = new ArrayList<>();
        Long productId1 = 1L;
        Long productId2 = 2L;
        items.add(new CartItem(7, productId1, new BigDecimal("1000"), cart));
        items.add(new CartItem(3, productId2, new BigDecimal("2000"), cart));
        cart.setItems(items);
        cart.setId(1L);
        cart.setUserId(userId);

        when(cartRepository.findCartByUserId(userId)).thenReturn(Optional.of(cart));

        List<CartItemDto> itemsDto = new ArrayList<>();
        Long productId3 = 3L;
        Long productId4 = 4L;
        itemsDto.add(new CartItemDto(3L, 10, productId3));
        itemsDto.add(new CartItemDto(4L, 5, productId4));

        Map<Long, BigDecimal> prices = new HashMap<>();

        when(productService.getPrices(anyList())).thenReturn(prices);

        RuntimeException rx = assertThrows(RuntimeException.class,
                () -> cartService.addToCart(itemsDto));

        assertEquals("Prices by product service is null", rx.getMessage());

        verify(productService).getPrices(anyList());
        verify(cartRepository, never()).save(any());

    }

    @Test
    void createOrder_shouldSendKafkaMessage() {


        CartDto cartDto = new CartDto();
        List<CartItemDto> items = new ArrayList<>();
        Long productId1 = 1L;
        Long productId2 = 2L;
        items.add(new CartItemDto(3L, 10, productId1));
        items.add(new CartItemDto(4L, 5, productId2));
        cartDto.setId(1L);
        cartDto.setUserId(1L);
        cartDto.setItems(items);

        when(cartQueryService.getCartByCurrentUser()).thenReturn(cartDto);
        when(redisTemplate.delete(anyString())).thenReturn(true);

        Cart cart = new Cart();
        cart.setId(1L);

        when(cartRepository.findCartByUserId(anyLong())).thenReturn(Optional.of(cart));

        Map<Long, ProductAvailability> mapProducts = new HashMap<>();
        mapProducts.put(productId1, new ProductAvailability(true, true));
        mapProducts.put(productId2, new ProductAvailability(true, true));
        CheckProductResponse response = new CheckProductResponse();
        response.setProductAvailability(mapProducts);

        when(productService.checkAvailability(any())).thenReturn(response);
        cartService.createOrder();

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);

        verify(cartQueryService).getCartByCurrentUser();
        verify(productService).checkAvailability(any());
        verify(kafkaProducer).sendMessage("cart-checkout", cartDto);
        verify(redisTemplate).delete(anyString());
        verify(cartRepository).save(captor.capture());

        Cart saved = captor.getValue();
        assertTrue(saved.getItems().isEmpty());

    }

    @Test
    void createOrder_shouldReturnException_whenProductNotEnough() {
        CartDto cartDto = new CartDto();
        List<CartItemDto> items = new ArrayList<>();
        Long productId1 = 1L;
        Long productId2 = 2L;
        items.add(new CartItemDto(3L, 10, productId1));
        items.add(new CartItemDto(4L, 5, productId2));
        cartDto.setId(1L);
        cartDto.setUserId(1L);
        cartDto.setItems(items);

        when(cartQueryService.getCartByCurrentUser()).thenReturn(cartDto);

        Map<Long, ProductAvailability> mapProducts = new HashMap<>();
        mapProducts.put(productId1, new ProductAvailability(true, false));
        mapProducts.put(productId2, new ProductAvailability(true, true));
        CheckProductResponse response = new CheckProductResponse();
        response.setProductAvailability(mapProducts);

        when(productService.checkAvailability(any())).thenReturn(response);

        assertThrows(NotEnoughException.class, () ->
                cartService.createOrder());


        verifyNoInteractions(kafkaProducer);
        verifyNoInteractions(redisTemplate);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void createOrder_shouldReturnException_whenProductNotExists() {
        CartDto cartDto = new CartDto();
        List<CartItemDto> items = new ArrayList<>();
        Long productId1 = 1L;
        Long productId2 = 2L;
        items.add(new CartItemDto(3L, 10, productId1));
        items.add(new CartItemDto(4L, 5, productId2));
        cartDto.setId(1L);
        cartDto.setUserId(1L);
        cartDto.setItems(items);

        when(cartQueryService.getCartByCurrentUser()).thenReturn(cartDto);

        Map<Long, ProductAvailability> mapProducts = new HashMap<>();
        mapProducts.put(productId1, new ProductAvailability(true, true));
        mapProducts.put(productId2, new ProductAvailability(false, false));
        CheckProductResponse response = new CheckProductResponse();
        response.setProductAvailability(mapProducts);

        when(productService.checkAvailability(any())).thenReturn(response);

        assertThrows(NotExistException.class, () ->
                cartService.createOrder());


        verifyNoInteractions(kafkaProducer);
        verifyNoInteractions(redisTemplate);
        verify(cartRepository, never()).save(any());

    }

    @Test
    void createOrder_shouldReturnException_whenCartHasNotProducts() {

        when(cartQueryService.getCartByCurrentUser()).thenReturn(new CartDto());

        assertThrows(CartHasNotProductsException.class, () ->
                cartService.createOrder());

        verifyNoInteractions(productService);
        verifyNoInteractions(kafkaProducer);
        verifyNoInteractions(redisTemplate);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void createOrder_shouldReturnException_whenCartIsNull() {

        when(cartQueryService.getCartByCurrentUser()).thenReturn(null);

        assertThrows(NotExistException.class, () ->
                cartService.createOrder());

        verifyNoInteractions(productService);
        verifyNoInteractions(kafkaProducer);
        verifyNoInteractions(redisTemplate);
        verify(cartRepository, never()).save(any());
    }
}
