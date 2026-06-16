package com.example.productservice.dto.address;

public record AddressDto(
        String country,
        String city,
        String address,
        String apartment,
        String postalCode
) {
}
