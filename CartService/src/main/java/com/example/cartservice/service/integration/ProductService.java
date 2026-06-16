package com.example.cartservice.service.integration;

import com.example.cartservice.dto.product.ProductDto;
import com.example.cartservice.dto.product.ProfileProducts;
import com.example.cartservice.dto.product.request.CheckProductRequest;
import com.example.cartservice.dto.product.request.CheckProductResponse;
import com.example.cartservice.exceptions.product.PriceServiceException;
import com.example.cartservice.feign.ProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductClient productClient;
    private final ProductCacheService productCacheService;

    public Map<Long, BigDecimal> getPrices(List<Long> ids){
        log.debug("GET_PRICES ids={}", ids);
        Map<Long, BigDecimal> pricesResponse = new HashMap<>();
        String key;
        BigDecimal price;
        List<Long> idsNotFound = new ArrayList<>();

        for(Long id: ids){
            key = "product:price:" + id;

            Object rawValue = redisTemplate.opsForValue().get(key);

            if (rawValue != null) {
                log.debug("PRICE_CACHE_HIT id={}", id);
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
                    throw new PriceServiceException("Product service exception");
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
            throw new PriceServiceException("Product service exception");
        }

        return pricesResponse;
    }

    public CheckProductResponse checkAvailability(CheckProductRequest request){
        log.info("CHECK_PRODUCT_AVAILABILITY");
        return productClient.getProductsAvailability(request);
    }


    public ProductDto getProduct(Long id){
        return productClient.getProduct(id);
    }

    public Map<Long, ProductDto> getProducts(List<Long> ids) {

        Map<Long, ProductDto> result = new HashMap<>();

        List<Long> idsToFetch = new ArrayList<>();

        for (Long id : ids) {

            ProductDto cached = productCacheService.get(id);

            if (cached != null) {
                result.put(id, cached);
            } else {
                idsToFetch.add(id);
            }

        }

        if (!idsToFetch.isEmpty()) {

            ProfileProducts profileProducts =
                    productClient.getProductsByIds(idsToFetch);

            List<ProductDto> products = profileProducts.productDtoList();

            for (ProductDto product : products) {

                productCacheService.save(product);

                result.put(
                        product.id(),
                        product
                );
            }

        }

        return result;
    }

}
