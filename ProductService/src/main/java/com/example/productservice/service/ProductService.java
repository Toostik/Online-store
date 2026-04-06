package com.example.productservice.service;

import com.example.productservice.dao.ProductRepository;
import com.example.productservice.dto.ProductDto;
import com.example.productservice.entity.Product;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductDto> getAllProducts() {
        List<Product> products = (List<Product>) productRepository.findAll();

        if(products.isEmpty()){
            throw new RuntimeException("Product list is empty");
        }

        List<ProductDto> productDtoList = products.stream()
                .map(Product::toDto)
                .toList();

        return productDtoList;
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Product by id{" + id + "}" + "doesn't exist")
        );
        return product.toDto();
    }

    public ProductDto getProductByName(String name){

        Product product = productRepository.findProductByName(name).orElseThrow(
                () -> new RuntimeException("Product by id{" + name + "}" + "doesn't exist")
        );

        return product.toDto();
    }


    public Map<Long, Long> getPrices(List<Long> ids) {
        return productRepository.findPricesByIds(ids)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }
}
