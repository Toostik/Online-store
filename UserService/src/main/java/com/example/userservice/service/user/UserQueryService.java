package com.example.userservice.service.user;

import com.example.userservice.dao.UserRepository;
import com.example.userservice.dto.orders.ProfileOrders;
import com.example.userservice.dto.user.UserDto;
import com.example.userservice.dto.user.UserProfile;
import com.example.userservice.dto.user.address.AddressDto;
import com.example.userservice.exceptions.UserNotFoundException;
import com.example.userservice.service.integration.OrderService;
import com.example.userservice.service.security.SecurityService;
import com.example.userservice.service.user.builder.UserProfileBuilder;
import com.example.userservice.service.user.cache.UserCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final OrderService orderService;
    private final UserCacheService userCacheService;
    private final UserProfileBuilder userProfileBuilder;


    public UserDto getUserById(Long userId) {

        log.debug("GET_USER_BY_ID id={}", userId);

        UserDto userDto = userCacheService.get(userId);

        return userDto;

    }

    public UserDto getUserByEmail(String email) {

        log.debug("GET_USER_BY_EMAIL email={}", email);

        Long userId = userRepository.findIdByEmail(email);

        if (userId == null) {

            log.warn("USER_NOT_FOUND email={}", email);

            throw new UserNotFoundException("User not found!");

        }

        return userCacheService.get(userId);

    }

    public UserDto getCurrentUser() {

        Long userId = securityService.getCurrentUserId();

        log.debug("CURRENT_USER_ID id={}", userId);

        return userCacheService.get(userId);

    }

    public UserProfile getProfile() {

        Long userId = securityService.getCurrentUserId();

        log.info("User get profile -> {}", userId);

        UserDto userDto = userCacheService.get(userId);

        ProfileOrders profileOrders = orderService.getRecentProducts();

        List<AddressDto> addresses = userDto.getAddresses();

        return userProfileBuilder.create(userDto, addresses, profileOrders);

    }
}
