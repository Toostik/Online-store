package com.example.orderservice.service.integration;

import com.example.orderservice.dto.cart.CartItemResponse;
import com.example.orderservice.dto.cart.CartResponse;
import com.example.orderservice.dto.product.ProfileProducts;
import com.example.orderservice.dto.product.request.CheckProductRequest;
import com.example.orderservice.dto.product.request.CheckProductResponse;
import com.example.orderservice.exceptions.product.ProductServiceException;
import com.example.orderservice.feign.ProductClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductClient productClient;

    public Map<Long, BigDecimal> getPrices(List<Long> ids){
        log.debug("GET_PRICES ids={}", ids);
        Map<Long, BigDecimal> pricesResponse = new HashMap<>();
        String key;
        BigDecimal price;
        List<Long> idsNotFound = new ArrayList<>();

        for(Long id: ids){
            log.debug("PRICE_CACHE_HIT id={}", id);

            key = "product:price:" + id;

            Object rawValue = redisTemplate.opsForValue().get(key);

            if (rawValue != null) {
                price = new BigDecimal(rawValue.toString());
                pricesResponse.put(id, price);
            } else {
                log.debug("PRICE_CACHE_MISS id={}", id);
                idsNotFound.add(id);
            }
        }

        try {
            if (!idsNotFound.isEmpty()) {
                log.info("CALL_PRODUCT_SERVICE prices ids={}", idsNotFound);
                Map<Long, BigDecimal> pricesFromProductService =
                        productClient.getPrices(idsNotFound);

                if(pricesFromProductService == null){
                    throw new ProductServiceException("Product service exception");
                }

                for(Long id: idsNotFound){
                    key = "product:price:" + id;

                    BigDecimal fetchedPrice = pricesFromProductService.get(id);

                    redisTemplate.opsForValue().set(
                            key,
                            fetchedPrice.toString(),
                            Duration.ofDays(1)
                    );

                    pricesResponse.put(id, fetchedPrice);
                }
            }

        } catch (Exception e){
            log.error("Failed to fetch prices", e);
            throw new ProductServiceException("Product service exception");
        }

        return pricesResponse;
    }

    public ProfileProducts getProductsById(List<Long> ids){
        return productClient.getProductsByIds(ids);
    }

    @Retry(name = "product-service")
    @CircuitBreaker(
            name = "product-service",
            fallbackMethod = "fallbackPrices"
    )
    public Map<Long, BigDecimal> loadPrices(List<Long> ids) {

        Map<Long, BigDecimal> prices = getPrices(ids);

        if (prices == null || prices.isEmpty()) {
            throw new ProductServiceException("Prices are empty");
        }

        return prices;
    }

    private Map<Long, BigDecimal> fallbackPrices(
            CartResponse cart,
            Exception ex
    ){
        throw new ProductServiceException(
                "Product service unavailable"
        );
    }

    public CheckProductResponse getAvailability(CheckProductRequest request){
        try {
            return productClient.getProductsAvailability(request);
        }
        catch (Exception e) {
            log.error("Availability error", e);
            throw e;
        }
    }

}

