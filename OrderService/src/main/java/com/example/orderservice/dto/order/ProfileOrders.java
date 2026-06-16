package com.example.orderservice.dto.order;

import java.util.List;

public record ProfileOrders(
        List<RecentOrderItemDto> listItems,
        Integer totalAmountOrders,
        Integer totalWishlistItems
) {
}
