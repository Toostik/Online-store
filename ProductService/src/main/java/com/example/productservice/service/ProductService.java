package com.example.productservice.service;

import com.example.productservice.dao.ProductRepository;
import com.example.productservice.dto.PriceDto;
import com.example.productservice.dto.ProductDto;
import com.example.productservice.dto.request.CreateProductRequest;
import com.example.productservice.dto.request.UpdateProductRequest;
import com.example.productservice.entity.ImageProduct;
import com.example.productservice.entity.Product;
import com.example.productservice.kafka.KafkaProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final KafkaProducer kafkaProducer;

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

    @Cacheable(value = "productDetail", key = "#id", unless = "#result == null")
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


    public Map<Long, BigDecimal> getPrices(List<Long> ids) {
        return productRepository.findPricesByIds(ids)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (BigDecimal) row[1]
                ));
    }

    public Boolean isProductExists(List<Long> ids) {
        for(Long l: ids){
            if(!productRepository.existsById(l)){
                return false;
            }
        }
        return true;
    }

    public void decreaseQuantity(Map<Long, Integer> products) {
        products.forEach((id, qty) -> {
            productRepository.findById(id).ifPresent(product -> {
                if((product.getStockQuantity() - qty) < 0)
                {
                    throw new RuntimeException("The product is not enough");
                }else {
                    product.setStockQuantity(product.getStockQuantity() - qty);
                    productRepository.save(product);
                }
            });
        });
    }

    public BigDecimal getPriceById(Long id) {
       Product product = productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Product doesn't exist")
        );
       return product.getPrice();
    }

    public void updateProduct(Long id, UpdateProductRequest request) {

        Product product = productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Product doesn't exist")
        );
        if(!request.getName().isBlank()) product.setName(request.getName());
        if(!request.getDescription().isBlank()) product.setDescription(request.getDescription());
        if(request.getPrice()!=null){
            product.setPrice(request.getPrice());
            kafkaProducer.sendMessage("product-price-updated", new PriceDto(product.getId(), product.getPrice()));
        }

        productRepository.save(product);


    }

    public ProductDto createProduct(CreateProductRequest request) {

        List<String> imagePaths = Optional.ofNullable(request.getImagePaths())
                .orElse(List.of());

        List<ImageProduct> images = imagePaths.stream()
                .map(ImageProduct::new).collect(Collectors.toList());

        Product product = request.toEntity(images);

        product.setCreatedAt(LocalDate.now());
        productRepository.save(product);
        return product.toDto();
    }
}
