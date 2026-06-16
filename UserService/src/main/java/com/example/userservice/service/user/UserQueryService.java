package com.example.userservice.service.user;

import com.example.userservice.dao.UserRepository;
import com.example.userservice.dto.orders.ProfileOrders;
import com.example.userservice.dto.user.UserDto;
import com.example.userservice.dto.user.UserProfile;
import com.example.userservice.dto.user.address.AddressDto;
import com.example.userservice.entity.user.User;
import com.example.userservice.exceptions.UserNotFoundException;
import com.example.userservice.service.integration.OrderService;
import com.example.userservice.service.security.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryService {
    private final UserRepository userRepository;
    @Qualifier("user")
    private final RedisTemplate<String, UserDto> redisTemplate;
    private final ObjectMapper objectMapper;
    private final SecurityService securityService;
    private final OrderService orderService;
    public UserDto getUserById(Long userId) {
        log.debug("GET_USER_BY_ID id={}", userId);
        String key = "user:" + userId;

        UserDto cachedUser = redisTemplate.opsForValue().get(key);

        if(cachedUser == null){
            log.debug("CACHE_MISS userId={}", userId);
            User user = userRepository.getUserById(userId).orElseThrow(
                    () -> new UserNotFoundException("User not found"));
            UserDto dto = user.toDto();
            redisTemplate.opsForValue().set(key, dto, Duration.ofDays(1));
            return dto;
        }

        log.debug("CACHE_HIT userId={}", userId);

        return cachedUser;
    }

    public UserDto getUserByEmail(String email) {
        log.debug("GET_USER_BY_EMAIL email={}", email);
        Long userId = userRepository.findIdByEmail(email);

        if (userId == null) {
            log.warn("USER_NOT_FOUND email={}", email);
            throw new UserNotFoundException("User not found!");
        }

        String key = "user:" + userId;
        Object cachedUser = redisTemplate.opsForValue().get(key);

        if (cachedUser == null) {
            User user = userRepository.getUserByEmail(email).orElseThrow(
                    () -> new UserNotFoundException("User not found")
            );

            redisTemplate.opsForValue().set(key, user.toDto(), Duration.ofDays(1));
            return user.toDto();
        }
        log.info("USER_GET_BY_EMAIL request email={}", email);
        return objectMapper.convertValue(cachedUser, UserDto.class);
    }

    public UserDto getCurrentUser() {
        Long userId = securityService.getCurrentUserId();
        log.debug("CURRENT_USER_ID id={}", userId);
        String key = "user:" + userId;

        Object cachedUser = redisTemplate.opsForValue().get(key);

        if (cachedUser == null) {

            User user = userRepository.findById(userId).orElseThrow(
                    () -> new UserNotFoundException("User not found!")
            );

            redisTemplate.opsForValue().set(key, user.toDto(), Duration.ofDays(1));
            return user.toDto();

        }

        return objectMapper.convertValue(cachedUser, UserDto.class);
    }

    public UserProfile getProfile() {

        Long userId = securityService.getCurrentUserId();

        log.info("User get profile -> {}", userId);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        ProfileOrders profileOrders = orderService.getRecentProducts();

        List<AddressDto> addresses = user.getAddresses()
                .stream()
                .map(address -> new AddressDto(
                        address.getType(),
                        address.getAddress()
                ))
                .toList();


        UserProfile userProfile = new UserProfile(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getPhone(),
                user.getAvatarImagePath(),
                user.getCreatedAt(),
                user.getSecurity().getStatus(),
                addresses,
                profileOrders.listItems(),
                profileOrders.totalAmountOrders(),
                profileOrders.totalWishlistItems()


        );

        return userProfile;
    }
}
