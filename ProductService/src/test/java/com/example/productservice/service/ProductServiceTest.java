package com.example.productservice.service;

import com.example.productservice.dao.ProductRepository;
import com.example.productservice.dto.PriceDto;
import com.example.productservice.dto.ProductDto;
import com.example.productservice.dto.request.CheckProductRequest;
import com.example.productservice.dto.request.CheckProductResponse;
import com.example.productservice.dto.request.ProductAvailability;
import com.example.productservice.dto.request.UpdateProductRequest;
import com.example.productservice.entity.Product;
import com.example.productservice.kafka.KafkaProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private KafkaProducer kafkaProducer;
    @InjectMocks
    private ProductService productService;

    @Test
    void getProductById_shouldReturnProductDto_whenProductExists(){
        Long id = 1L;

        Product product = new Product();
        product.setId(id);
        product.setName("iPhone");

        when(productRepository.findById(id))
                .thenReturn(Optional.of(product));

        ProductDto result = productService.getProductById(id);

        assertNotNull(result);
        Assertions.assertEquals("iPhone", result.getName());

        verify(productRepository).findById(id);
    }
    @Test
    void getProductById_shouldReturnNull_whenProductNotExists(){
        Long id = 1L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException rx = assertThrows(RuntimeException.class, () ->
           productService.getProductById(id)
        );

        assertEquals("Product not found!", rx.getMessage());

        verify(productRepository).findById(id);
    }
    @Test
    void updateProduct_shouldSendKafkaMessage_whenPriceUpdated(){
        Long id =1L;

        Product product = new Product();
        product.setId(id);
        product.setPrice(BigDecimal.valueOf(100));

        UpdateProductRequest request = new UpdateProductRequest();
        request.setPrice(BigDecimal.valueOf(200));

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        productService.updateProduct(id, request);

        verify(kafkaProducer).sendMessage(
                eq("product-price-updated"),
                any(PriceDto.class)
        );

        verify(productRepository).findById(id);
        verify(productRepository).save(product);
    }

    @Test
    void updateProduct_shouldNotSendKafkaMessage_whenPriceNotUpdated(){
        Long id = 1L;

        Product product = new Product();
        product.setId(id);
        product.setPrice(BigDecimal.valueOf(100));

        UpdateProductRequest request = new UpdateProductRequest();
        request.setDescription("New product");

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        productService.updateProduct(id, request);

        verify(kafkaProducer, (never())).sendMessage(any(),any());
        verify(productRepository).findById(id);
        verify(productRepository).save(product);
    }
    @Test
    void updateProduct_shouldSendCorrectKafkaMessage(){
        Long id =1L;

        Product product = new Product();
        product.setId(id);
        product.setPrice(BigDecimal.valueOf(100));

        UpdateProductRequest request = new UpdateProductRequest();
        request.setPrice(BigDecimal.valueOf(200));

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        productService.updateProduct(id, request);

        ArgumentCaptor<PriceDto> captor = ArgumentCaptor.forClass(PriceDto.class);

        verify(kafkaProducer).sendMessage(
                eq("product-price-updated"),
                captor.capture()
        );

        PriceDto priceDto = captor.getValue();

        assertEquals(id, priceDto.getId());
        assertEquals(BigDecimal.valueOf(200), priceDto.getNewPrice());

        verify(productRepository).findById(id);
        verify(productRepository).save(product);
    }
    private Product createProduct(Long id, int stock, String name){
        Product product = new Product();
        product.setId(id);
        product.setStockQuantity(stock);
        product.setName(name);
        return product;
    }

    @Test
    void getProductAvailability_shouldReturnAvailableProducts_whenEnoughStock(){
        Long id1 = 1L;
        Long id2 = 2L;

        Product product1 = createProduct(id1, 5, "iPhone");
        Product product2 = createProduct(id1, 3, "Samsung");

        when(productRepository.findById(id1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(id2)).thenReturn(Optional.of(product2));

        CheckProductRequest request = new CheckProductRequest();
        Map<Long, Integer> products = new HashMap<>();
        products.put(id1, 3);
        products.put(id2, 1);
        request.setProducts(products);

        CheckProductResponse response = productService.getProductAvailability(request);

        assertNotNull(response.getProductAvailability());

        ProductAvailability availability1 = response.getProductAvailability().get(id1);
        ProductAvailability availability2 = response.getProductAvailability().get(id2);

        assertTrue(availability1.isExists());
        assertTrue(availability1.isEnoughStock());

        assertTrue(availability2.isExists());
        assertTrue(availability2.isEnoughStock());

        verify(productRepository).findById(id1);
        verify(productRepository).findById(id2);
    }
    @Test
    void getProductAvailability_shouldMarkProductAsNotExists_whenProductMissing(){
        Long id1 = 1L;
        Long id2 = 4L;

        Product product1 = createProduct(id1, 5, "iPhone");


        when(productRepository.findById(id1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(id2)).thenReturn(Optional.empty());

        CheckProductRequest request = new CheckProductRequest();
        Map<Long, Integer> products = new HashMap<>();
        products.put(id1, 3);
        products.put(id2, 1);
        request.setProducts(products);


        RuntimeException rx = assertThrows(RuntimeException.class,
                () -> productService.getProductAvailability(request));

        assertEquals("Product not found!", rx.getMessage());

        verify(productRepository).findById(id1);
        verify(productRepository).findById(id2);
    }
    @Test
    void getProductAvailability_shouldMarkProductAsNotEnough_whenProductTooHigh(){
        Long id1 = 1L;
        Long id2 = 2L;

        Product product1 = createProduct(id1, 5, "iPhone");
        Product product2 = createProduct(id1, 3, "Samsung");

        when(productRepository.findById(id1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(id2)).thenReturn(Optional.of(product2));

        CheckProductRequest request = new CheckProductRequest();
        Map<Long, Integer> products = new HashMap<>();
        products.put(id1, 7);
        products.put(id2, 4);
        request.setProducts(products);

        CheckProductResponse response = productService.getProductAvailability(request);

        assertNotNull(response.getProductAvailability());

        ProductAvailability availability1 = response.getProductAvailability().get(id1);
        ProductAvailability availability2 = response.getProductAvailability().get(id2);

        assertTrue(availability1.isExists());
        assertFalse(availability1.isEnoughStock());

        assertTrue(availability2.isExists());
        assertFalse(availability2.isEnoughStock());

        verify(productRepository).findById(id1);
        verify(productRepository).findById(id2);
    }
}
