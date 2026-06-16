package com.example.userservice.dto.user.address;

import com.example.userservice.entity.enums.AddressType;

public record AddressDto(AddressType type, String address) {
}
