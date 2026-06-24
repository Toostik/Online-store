package com.example.orderservice.service.order.cache;

import com.example.orderservice.dto.order.OrderDto;
import com.example.orderservice.entity.order.Order;
import com.example.orderservice.entity.order.mapper.OrderMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.CompositeByteBuf;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCacheService {

    private final RedisTemplate<String,Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final OrderMapper orderMapper;

    public void save(Order order){

        Long userId = order.getUserId();

        redisTemplate.opsForList()
                .rightPush(
                        OrderRedisKeys.userOrders(userId),
                        order.getId()
                );

        redisTemplate.expire(
                OrderRedisKeys.userOrders(userId),
                Duration.ofDays(1)
        );

        redisTemplate.opsForValue()
                .set(
                        OrderRedisKeys.order(order.getId()),
                        orderMapper.toDto(order),
                        Duration.ofHours(1)
                );
    }

    public OrderDto getOrder(Long id){

        Object cached =
                redisTemplate.opsForValue()
                        .get(
                                OrderRedisKeys.order(id)
                        );

        if(cached == null){
            return null;
        }

        return objectMapper.convertValue(
                cached,
                OrderDto.class
        );
    }


}
