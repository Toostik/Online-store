package com.example.productservice.service;

import com.example.productservice.dao.CategoryRepository;
import com.example.productservice.dao.ProductRepository;
import com.example.productservice.dto.CategoryDto;
import com.example.productservice.dto.PriceDto;
import com.example.productservice.dto.ProductDto;
import com.example.productservice.dto.request.*;
import com.example.productservice.entity.Category;
import com.example.productservice.entity.ImageProduct;
import com.example.productservice.entity.Product;
import com.example.productservice.exceptions.CategoryNotFoundException;
import com.example.productservice.kafka.KafkaProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
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
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

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

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            throw new RuntimeException("Product not found!");
        }

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
        return productRepository.getPriceById(id);
    }

    public void updateProduct(Long id, UpdateProductRequest request) {

        Product product = productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Product doesn't exist")
        );

        if(request.getName() != null && !request.getName().isBlank()) product.setName(request.getName());
        if(request.getDescription() != null && !request.getDescription().isBlank()) product.setDescription(request.getDescription());
        if(request.getPrice()!=null){
            product.setPrice(request.getPrice());
            kafkaProducer.sendMessage("product-price-updated", new PriceDto(product.getId(), product.getPrice()));
        }

        productRepository.save(product);


    }

    public ProductDto createProduct(CreateProductRequest request) {

        List<String> imagePaths = Optional.ofNullable(request.getImagePaths())
                .orElse(List.of());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found!"));

        List<ImageProduct> images = imagePaths.stream()
                .map(ImageProduct::new)
                .toList();

        Product product;

        if (!images.isEmpty()) {
            product = request.toEntity(images, category);
        } else {
            product = request.toEntityWithoutImages(category);
        }

        product.setCreatedAt(LocalDate.now());

        productRepository.save(product);

        return product.toDto();
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public CheckProductResponse getProductAvailability(CheckProductRequest request) {
        Map<Long, Integer> products = request.getProducts();
        CheckProductResponse response = new CheckProductResponse();
        Map<Long, ProductAvailability> availabilityMap = new HashMap<>();
        products.forEach((productId, quantity) ->
        {
            ProductAvailability availability = new ProductAvailability();
            availability.setExists(true);
            availability.setEnoughStock(true);
            ProductDto productDto = getProductById(productId);

            if (productDto == null) {
                availability.setExists(false);
                availability.setEnoughStock(false);
            } else {
                availability.setExists(true);
                availability.setEnoughStock(productDto.getStockQuantity() >= quantity);
            }

            availabilityMap.put(productId, availability);
        });
        response.setProductAvailability(availabilityMap);
        return response;
    }
}
