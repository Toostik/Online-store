package com.example.productservice.dto.product.request;

import com.example.productservice.dto.product.ProductAvailability;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckProductResponse {
    Map<Long, ProductAvailability> productAvailability;
}
