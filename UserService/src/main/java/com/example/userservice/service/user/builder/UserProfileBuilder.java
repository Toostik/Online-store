package com.example.userservice.service.user.builder;

import com.example.userservice.dto.orders.ProfileOrders;
import com.example.userservice.dto.user.UserDto;
import com.example.userservice.dto.user.UserProfile;
import com.example.userservice.dto.user.address.AddressDto;
import com.example.userservice.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileBuilder {

    public UserProfile create(UserDto userDto, List<AddressDto> addresses, ProfileOrders profileOrders){
        return new UserProfile(
                userDto.getId(),
                userDto.getEmail(),
                userDto.getUsername(),
                userDto.getPhone(),
                userDto.getAvatarImagePath(),
                userDto.getCreatedAt(),
                userDto.getSecurityStatus(),
                addresses,
                profileOrders.listItems(),
                profileOrders.totalAmountOrders(),
                profileOrders.totalWishlistItems()

        );
    }
}
