package com.example.orderservice.dto.address;

public record AddressDto(
        String country,
        String city,
        String address,
        String apartment,
        String postalCode
) {
}
