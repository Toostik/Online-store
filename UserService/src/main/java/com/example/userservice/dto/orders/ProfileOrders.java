package com.example.userservice.dto.orders;

import java.util.List;

public record ProfileOrders(
        List<RecentOrderItemDto> listItems,
        Integer totalAmountOrders,
        Integer totalWishlistItems
) {
}
