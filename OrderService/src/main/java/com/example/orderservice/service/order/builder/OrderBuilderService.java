package com.example.orderservice.service.order.builder;

import com.example.orderservice.dto.cart.CartItemResponse;
import com.example.orderservice.dto.cart.CartResponse;
import com.example.orderservice.entity.order.Order;
import com.example.orderservice.entity.order.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrderBuilderService {

    public Order build(
            CartResponse cart,
            Map<Long, BigDecimal> prices,
            Long userId
    ) {

        Order order = new Order();

        order.setUserId(userId);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;

        for(CartItemResponse item : cart.getItems()) {

            BigDecimal price = prices.get(item.getProductId());

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPriceAtPurchase(price);

            items.add(orderItem);

            total = total.add(
                    price.multiply(
                            BigDecimal.valueOf(item.getQuantity())
                    )
            );
        }

        order.setItems(items);
        order.setTotalAmount(total);

        return order;
    }
}
