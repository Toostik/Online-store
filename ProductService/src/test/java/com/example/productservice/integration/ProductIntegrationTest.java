package com.example.productservice.integration;

import com.example.productservice.dao.CategoryRepository;
import com.example.productservice.dao.ProductRepository;
import com.example.productservice.dto.CategoryDto;
import com.example.productservice.entity.Category;
import com.example.productservice.entity.Product;
import com.example.productservice.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(
        properties = {
                "spring.kafka.listener.auto-startup=false",
                "spring.cache.type=none"}
)
@AutoConfigureMockMvc
public class ProductIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("test-db")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void clearDb() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_shouldSaveToDatabase() throws Exception {

        Category category = new Category();
        category.setName("Phones");
        Category saved = categoryRepository.save(category);

        String json = """
                {
                    "name": "iPhone",
                    "price": 1000,
                    "stockQuantity": 10,
                    "categoryId": %d
                }
                """.formatted(saved.getId());
        ;

        mockMvc.perform(post("/api/products/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("iPhone"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.stockQuantity").value(10))
                .andExpect(jsonPath("$.categoryId").value(1));

        List<Product> products = (List<Product>) productRepository.findAll();

        assertEquals(1, products.size());
        assertEquals("iPhone", products.getFirst().getName());
        BigDecimal expected = new BigDecimal(1000);
        assertEquals(0, products.getFirst().getPrice().compareTo(expected));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_shouldFail_whenNameIsEmpty() throws Exception {

        Category category = new Category();
        category.setName("Phones");
        Category saved = categoryRepository.save(category);

        String json = """
                {
                    "name": "",
                    "price": 1000,
                    "stockQuantity": 10,
                    "categoryId": %d
                }
                """.formatted(saved.getId());


        mockMvc.perform(post("/api/products/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());


    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_shouldFail_whenCategoryNotExists() throws Exception {

        String json = """
                {
                    "name": "iPhone",
                    "price": 1000,
                    "stockQuantity": 10,
                    "categoryId": 102
                }
                """;

        mockMvc.perform(post("/api/products/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_shouldFail_whenCategoryIsEmpty() throws Exception {

        String json = """
                {
                    "name": "iPhone",
                    "price": 1000,
                    "stockQuantity": 10,
                }
                """;

        mockMvc.perform(post("/api/products/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPriceById_shouldReturnBigDecimal() throws Exception {
        Category category = new Category();
        category.setName("Phones");
        Category savedCategory = categoryRepository.save(category);


        Product product = new Product();
        product.setName("iPhone");
        product.setCreatedAt(LocalDate.now());
        product.setCategory(savedCategory);
        product.setPrice(new BigDecimal(100));

        Product saved = productRepository.save(product);

        mockMvc.perform(get("/api/products/price/{id}", saved.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("100.00"));
    }

}
