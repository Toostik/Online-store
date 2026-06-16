package com.example.userservice.dto.user;

import com.example.userservice.dto.user.address.AddressDto;
import com.example.userservice.dto.orders.RecentOrderItemDto;
import com.example.userservice.entity.enums.UserSecurityStatus;

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
        UserSecurityStatus securityStatus,
        List<AddressDto> savedAddresses,
        //Recent Products
        List<RecentOrderItemDto> recentOrderItems,
        //Orders Info
        Integer totalAmountOrders,
        Integer wishlist


) {
}
