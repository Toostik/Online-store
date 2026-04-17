package com.example.productservice.controller;

import com.example.productservice.config.JwtConfig;
import com.example.productservice.config.SecurityConfig;
import com.example.productservice.dto.ProductDto;
import com.example.productservice.dto.request.CreateProductRequest;
import com.example.productservice.entity.Product;
import com.example.productservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductService productService;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser

    void getProductById_shouldReturnProduct(){
        Long id = 1L;

        ProductDto dto = new ProductDto();
        dto.setId(id);
        dto.setName("iPhone");


        when(productService.getProductById(id)).thenReturn(dto);

        try {
            mockMvc.perform(get("/api/products/{id}",id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("iPhone"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    @WithMockUser
    void getAllProductById_shouldReturnListOfProduct(){

        ProductDto dto1 = new ProductDto();
        dto1.setId(1L);
        dto1.setName("iPhone");
        ProductDto dto2 = new ProductDto();
        dto2.setId(2L);
        dto2.setName("Samsung");


        when(productService.getAllProducts()).thenReturn(List.of(dto1,dto2));

        try {
            mockMvc.perform(get("/api/products/all"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("iPhone"))
                    .andExpect(jsonPath("$[1].name").value("Samsung"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    @WithMockUser
    void createProduct_shouldReturnProductDto(){
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Product");
        request.setDescription("Description");

        ProductDto responseDto = new ProductDto();
        responseDto.setId(1L);
        responseDto.setName("Product");

        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(responseDto);

        try {
            mockMvc.perform(post("/api/products/create")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Product"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
