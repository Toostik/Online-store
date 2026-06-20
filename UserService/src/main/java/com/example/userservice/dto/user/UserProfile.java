package com.example.userservice.dto.user;

import com.example.userservice.dto.user.address.AddressDto;
import com.example.userservice.dto.orders.RecentOrderItemDto;

import java.time.LocalDateTime;
import java.util.List;

public record UserProfile(
        //User
        Long id,
        String email,
        String username,
        String phone,
        String avatarImage,
        LocalDateTime createdAt,
        String securityStatus,
        List<AddressDto> savedAddresses,
        //Recent Products
        List<RecentOrderItemDto> recentOrderItems,
        //Orders Info
        Integer totalAmountOrders,
        Integer wishlist


) {
}
