package com.example.orderservice.service.order.builder;

import com.example.orderservice.dto.cart.CartItemResponse;
import com.example.orderservice.dto.cart.CartResponse;
import com.example.orderservice.entity.address.Address;
import com.example.orderservice.entity.delivery.Delivery;
import com.example.orderservice.entity.enums.ItemShipmentStatus;
import com.example.orderservice.entity.enums.OrderStatus;
import com.example.orderservice.entity.order.Order;
import com.example.orderservice.entity.order.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.example.events.flashsale.FlashSaleReservationAndCheckoutEvent;
import org.example.events.order.AddressDto;
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

    public Order buildFlashSaleOrder(FlashSaleReservationAndCheckoutEvent event, BigDecimal price) {

        Order order = Order.builder()
                .orderStatus(OrderStatus.CREATED)
                .userId(event.userId())
                .createdAt(LocalDateTime.now())
                .totalAmount(price.multiply(BigDecimal.valueOf(event.quantity())))
                .build();

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .status(ItemShipmentStatus.NOT_SHIPPED)
                .productId(event.productId())
                .quantity(event.quantity())
                .priceAtPurchase(price)
                .build();

        order.setItems(List.of(orderItem));

        AddressDto addressDto = event.address();

        Address address = Address.builder()
                .userId(event.userId())
                .country(addressDto.country())
                .address(addressDto.address())
                .city(addressDto.city())
                .apartment(addressDto.apartment())
                .postalCode(addressDto.postalCode())
                .build();

        Delivery delivery = Delivery.builder()
                .type(event.deliveryMethod())
                .price(new BigDecimal("995"))
                .estimatedDays(3)
                .build();

        order.setAddress(address);
        order.setDelivery(delivery);

        return order;
    }
}
