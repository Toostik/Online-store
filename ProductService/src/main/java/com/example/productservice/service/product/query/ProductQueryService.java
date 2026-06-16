package com.example.productservice.service.product.query;

import com.example.productservice.dao.product.ProductRepository;
import com.example.productservice.dao.wishlist.WishlistRepository;
import com.example.productservice.dto.product.ProductDto;
import com.example.productservice.dto.product.ProfileProducts;
import com.example.productservice.entity.product.Product;
import com.example.productservice.entity.product.mapper.ProductMapper;
import com.example.productservice.exceptions.product.ProductNotFoundException;
import com.example.productservice.service.product.cache.ProductCacheService;
import com.example.productservice.service.security.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {

    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;
    private final ProductMapper productMapper;

    private final SecurityService securityService;
    private final ProductCacheService productCacheService;


    public List<ProductDto> getAllProducts() {

        Iterable<Product> products = productRepository.findAll();

        return StreamSupport.stream(products.spliterator(), false)
                .map(productMapper::toDto)
                .toList();

    }

    public ProductDto getProductById(Long id) {

        ProductDto product = productCacheService.get(id);

        if (product == null) {

            Product productFromDb = productRepository.findById(id).orElseThrow(
                    () -> new ProductNotFoundException(id));

            ProductDto productDto = productMapper.toDto(productFromDb);

            productCacheService.save(productDto);

            return productDto;
        }

        return product;
    }

    public Map<Long, BigDecimal> getPrices(List<Long> ids) {

        return productRepository.findPricesByIds(ids)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (BigDecimal) row[1]
                ));

    }

    public BigDecimal getPriceById(Long id) {
        return productRepository.getPriceById(id);
    }

    public Page<ProductDto> getPageProducts(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return productRepository
                .getProductsWithImages(pageable)
                .map(productMapper::toDto);

    }

    public List<ProductDto> getListProductsWithImages(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.getProductWithImage(pageable).stream().map(productMapper::toDto).toList();
    }

    public ProfileProducts getProductsByIds(List<Long> ids) {

        Long userId = securityService.getCurrentUserId();

        Map<Long, ProductDto> cachedProducts =
                productCacheService.getProducts(ids);

        List<Long> missedIds = ids.stream()
                .filter(id -> !cachedProducts.containsKey(id))
                .toList();

        List<ProductDto> missedProducts =
                productRepository.findAllByIdIn(missedIds)
                        .stream()
                        .map(productMapper::toDto)
                        .toList();

        missedProducts.forEach(productCacheService::save);

        missedProducts.forEach(dto ->
                cachedProducts.put(dto.id(), dto)
        );

        List<ProductDto> products = ids.stream()
                .map(cachedProducts::get)
                .toList();

        return new ProfileProducts(
                products,
                wishlistRepository.countByUserId(userId)
        );

    }
}
