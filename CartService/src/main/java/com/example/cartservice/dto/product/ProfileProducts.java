package com.example.cartservice.dto.product;

import java.util.List;

public record ProfileProducts(
        List<ProductDto> productDtoList,
        Integer totalWishlistItems
) {
}
