package com.example.orderservice.dto.order;

import com.example.orderservice.dto.cart.CartResponse;

import java.math.BigDecimal;
import java.util.Map;

public record OrderContext(CartResponse cart, Map<Long, BigDecimal> prices) {
}
