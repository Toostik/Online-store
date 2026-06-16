package com.example.productservice.dto.product;

import java.util.List;

public record ProfileProducts(
        List<ProductDto> productDtoList,
        Integer totalWishlistItems
) {
}
