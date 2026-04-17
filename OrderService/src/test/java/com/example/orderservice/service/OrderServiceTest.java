package com.example.orderservice.service;

import com.example.orderservice.dao.OrderRepository;
import com.example.orderservice.dto.CartDto;
import com.example.orderservice.dto.CartItemDto;
import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.dto.OrderItemDto;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.entity.Status;
import com.example.orderservice.kafka.KafkaProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private KafkaProducer kafkaProducer;
    @Mock PriceService priceService;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ListOperations<String, Object> listOps;

    @Mock
    private ValueOperations<String, Object> valueOps;
    @Mock
    private ObjectMapper objectMapper;
    @Spy
    @InjectMocks
    private OrderService orderService;

    private CartDto createCart(){
        CartItemDto cartItemDto = new CartItemDto(1L, 10, 1L);
        List<CartItemDto> items =  List.of(cartItemDto);

        return new CartDto(1L,1L, items);
    }

    private Map<Long, BigDecimal> createPrices(){

        Map<Long, BigDecimal> prices = new HashMap<>();
        prices.put(1L, new BigDecimal("1000"));

        return prices;
    }

    @Test
    void createOrder_shouldReturnOrderDto(){

        CartDto cartDto = createCart();
        Map<Long, BigDecimal> prices = createPrices();

        when(priceService.getPrices(anyList())).thenReturn(prices);

        OrderDto result = orderService.createOrder(cartDto);

        assertEquals(result.getUserId(), cartDto.getUserId());
        assertEquals("CREATED", result.getStatus());
        assertEquals(new BigDecimal("10000"), result.getTotalAmount());

        OrderItemDto orderItemDto = result.getItems().getFirst();
        assertEquals(orderItemDto.getProduct_id(), cartDto.getItems().getFirst().getProductId());
        assertEquals(orderItemDto.getQuantity(), cartDto.getItems().getFirst().getQuantity());
        assertEquals(new BigDecimal("1000"), orderItemDto.getPriceAtPurchase());

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

        verify(priceService).getPrices(anyList());
        verify(orderRepository).save(captor.capture());

        Order saved = captor.getValue();

        assertEquals(cartDto.getUserId(), saved.getUserId());
        assertEquals(new BigDecimal("10000"), saved.getTotalAmount());
        assertEquals(1, saved.getItems().size());
    }

    @Test
    void createOrder_shouldReturnOrderDto_whenCartHasTwoProduct(){

        CartDto cartDto = createCart();
        CartItemDto cartItemDto1 = new CartItemDto(1L, 10, 1L);
        CartItemDto cartItemDto2 = new CartItemDto(2L, 6, 2L);
        cartDto.setItems(List.of(cartItemDto1, cartItemDto2));

        Map<Long, BigDecimal> prices = createPrices();
        prices.put(2L, new BigDecimal("2000"));

        when(priceService.getPrices(anyList())).thenReturn(prices);

        OrderDto result = orderService.createOrder(cartDto);

        assertEquals(result.getUserId(), cartDto.getUserId());
        assertEquals("CREATED", result.getStatus());
        assertEquals(new BigDecimal("22000"), result.getTotalAmount());
        assertEquals(2, result.getItems().size());

        OrderItemDto orderItemDto = result.getItems().getFirst();
        assertEquals(orderItemDto.getProduct_id(), cartDto.getItems().getFirst().getProductId());
        assertEquals(orderItemDto.getQuantity(), cartDto.getItems().getFirst().getQuantity());
        assertEquals(new BigDecimal("1000"), orderItemDto.getPriceAtPurchase());

        OrderItemDto orderItemDto2 = result.getItems().getLast();
        assertEquals(orderItemDto2.getProduct_id(), cartDto.getItems().getLast().getProductId());
        assertEquals(orderItemDto2.getQuantity(), cartDto.getItems().getLast().getQuantity());
        assertEquals(new BigDecimal("2000"), orderItemDto2.getPriceAtPurchase());

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

        verify(priceService).getPrices(anyList());
        verify(orderRepository).save(captor.capture());

        Order saved = captor.getValue();

        assertEquals(cartDto.getUserId(), saved.getUserId());
        assertEquals(new BigDecimal("22000"), saved.getTotalAmount());
        assertEquals(2, saved.getItems().size());
    }

    @Test
    void createOrder_shouldSendKafkaMessage(){

        CartDto cartDto = createCart();
        Map<Long, BigDecimal> prices = createPrices();

        when(priceService.getPrices(anyList())).thenReturn(prices);

        OrderDto result = orderService.createOrder(cartDto);

        verify(kafkaProducer).sendMessage("orders-created", result);
        verify(priceService).getPrices(anyList());
    }

    @Test
    void createOrder_shouldReturnException_whenCartItemsIsEmpty(){

        CartDto cartDto = createCart();
        cartDto.setItems(List.of());

        RuntimeException rx = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(cartDto));

        assertEquals("Cart is empty!",rx.getMessage());

        verifyNoInteractions(priceService);
        verifyNoInteractions(kafkaProducer);
        verifyNoInteractions(orderRepository);
    }

    @Test
    void createOrder_shouldReturnException_whenPricesIsEmpty(){

        CartDto cartDto = createCart();

        Map<Long, BigDecimal> prices = new HashMap<>();

        when(priceService.getPrices(anyList())).thenReturn(prices);

        RuntimeException rx = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(cartDto));

        assertEquals("Prices is empty!", rx.getMessage());

    }
//    Get All Orders
    @Test
    void getAllOrders_shouldReturnListOfOrderDto(){
        Long userId = 1L;

        Order order1 = new Order();
        order1.setUserId(userId);
        order1.setStatus(Status.CREATED);
        order1.setTotalAmount(new BigDecimal("20000"));

        OrderItem item1 = new OrderItem();
        item1.setProductId(1L);
        item1.setQuantity(4);
        order1.setItems(new ArrayList<>(List.of(item1)));


        Order order2 = new Order();
        order2.setUserId(userId);
        order2.setStatus(Status.CONFIRMED);
        order2.setTotalAmount(new BigDecimal("30000"));

        OrderItem item2 = new OrderItem();
        item2.setProductId(1L);
        item2.setQuantity(4);
        order2.setItems(new ArrayList<>(List.of(item2)));

        List<Order> orders = new ArrayList<>(List.of(order1, order2));

        doReturn(1L).when(orderService).getCurrentUserId();
        when(orderRepository.findAllByUserId(userId)).thenReturn(orders);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.range(anyString(), anyLong(), anyLong()))
                .thenReturn(List.of());

        List<OrderDto> result = orderService.getAllOrdersOfCurrentUser();

        OrderDto orderDto1 = result.getFirst();
        OrderDto orderDto2 = result.getLast();

        assertEquals(orders.size(), result.size());

        assertEquals(userId, orderDto1.getUserId());
        assertEquals(userId, orderDto2.getUserId());
        assertEquals("CREATED", orderDto1.getStatus());
        assertEquals("CONFIRMED", orderDto2.getStatus());
        assertEquals(new BigDecimal("20000"), orderDto1.getTotalAmount());
        assertEquals(new BigDecimal("30000"), orderDto2.getTotalAmount());

        verify(orderRepository).findAllByUserId(userId);
        verify(orderService).getCurrentUserId();
        verify(valueOps, times(2)).set(anyString(), any(), any());
        verify(listOps, times(2)).rightPush(anyString(), any());
    }
    @Test
    void getAllOrders_shouldReturnListOfOrderDto_whenCached(){
        Long userId = 1L;

        Order order1 = new Order();
        order1.setId(1L);
        order1.setUserId(userId);
        order1.setStatus(Status.CREATED);
        order1.setTotalAmount(new BigDecimal("20000"));

        OrderItem item1 = new OrderItem();
        item1.setProductId(1L);
        item1.setQuantity(4);
        order1.setItems(new ArrayList<>(List.of(item1)));


        Order order2 = new Order();
        order2.setId(2L);
        order2.setUserId(userId);
        order2.setStatus(Status.CONFIRMED);
        order2.setTotalAmount(new BigDecimal("30000"));

        OrderItem item2 = new OrderItem();
        item2.setProductId(1L);
        item2.setQuantity(4);
        order2.setItems(new ArrayList<>(List.of(item2)));

        doReturn(1L).when(orderService).getCurrentUserId();

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.range(anyString(), anyLong(), anyLong()))
                .thenReturn(List.of(order1.getId(), order2.getId()));

        when(valueOps.multiGet(anyList()))
                .thenReturn(List.of(order1.toDto(),order2.toDto()));

        when(objectMapper.convertValue(any(), eq(OrderDto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<OrderDto> result = orderService.getAllOrdersOfCurrentUser();

        OrderDto orderDto1 = result.getFirst();
        OrderDto orderDto2 = result.getLast();

        assertEquals(2, result.size());

        assertEquals(userId, orderDto1.getUserId());
        assertEquals(userId, orderDto2.getUserId());
        assertEquals("CREATED", orderDto1.getStatus());
        assertEquals("CONFIRMED", orderDto2.getStatus());
        assertEquals(new BigDecimal("20000"), orderDto1.getTotalAmount());
        assertEquals(new BigDecimal("30000"), orderDto2.getTotalAmount());

        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);

        verifyNoInteractions(orderRepository);
        verify(orderService).getCurrentUserId();
        verify(valueOps).multiGet(captor.capture());

        List<String> keys = captor.getValue();
        assertTrue(keys.contains("order:1"));
        assertTrue(keys.contains("order:2"));
    }
    @Test
    void getAllOrders_shouldReturnException_whenUserHasNotOrders(){
        Long userId = 1L;

        doReturn(1L).when(orderService).getCurrentUserId();

        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.range(anyString(), anyLong(), anyLong()))
                .thenReturn(List.of());

        when(orderRepository.findAllByUserId(userId)).thenReturn(List.of());

        List<OrderDto> result = orderService.getAllOrdersOfCurrentUser();

        assertTrue(result.isEmpty());

        verify(orderRepository).findAllByUserId(userId);
        verify(orderService).getCurrentUserId();

    }
}
