package com.example.productservice.service.inventory.query;

import com.example.productservice.dao.product.ProductRepository;
import com.example.productservice.dto.product.ProductAvailability;
import com.example.productservice.dto.product.request.CheckProductRequest;
import com.example.productservice.dto.product.request.CheckProductResponse;
import com.example.productservice.entity.product.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryQueryService {

    private final ProductRepository productRepository;

    public Boolean isProductExists(List<Long> ids) {

        return productRepository.countByIdIn(ids)
                == ids.size();

    }



    public CheckProductResponse getProductAvailability(CheckProductRequest request) {

        List<Product> products =
                productRepository.findAllByIdIn(
                        new ArrayList<>(request.getProducts().keySet())
                );

        Map<Long, Product> productMap =
                products.stream()
                        .collect(Collectors.toMap(
                                Product::getId,
                                Function.identity()
                        ));

        Map<Long, ProductAvailability> availabilityMap = new HashMap<>();

        request.getProducts().forEach((productId, quantity) -> {

            Product product = productMap.get(productId);

            ProductAvailability availability = new ProductAvailability();

            if (product == null) {

                availability.setExists(false);
                availability.setEnoughStock(false);

            } else {

                availability.setExists(true);
                availability.setEnoughStock(
                        product.getAvailableQuantity() >= quantity
                );
            }

            availabilityMap.put(productId, availability);

        });

        CheckProductResponse response = new CheckProductResponse();
        response.setProductAvailability(availabilityMap);

        return response;
    }

}
