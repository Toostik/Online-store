package org.example.events.order;

public record AddressDto(
        String country,
        String city,
        String address,
        String apartment,
        String postalCode
) {
}
