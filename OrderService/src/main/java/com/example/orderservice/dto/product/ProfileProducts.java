package com.example.orderservice.dto.product;

import java.util.List;

public record ProfileProducts(
        List<ProductDto> productDtoList,
        Integer totalWishlistItems
) {
}
