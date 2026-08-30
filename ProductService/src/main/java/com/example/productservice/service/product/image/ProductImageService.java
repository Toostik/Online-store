package com.example.productservice.service.product.image;

import com.example.productservice.dao.product.ImageProductRepository;
import com.example.productservice.dao.product.ProductRepository;
import com.example.productservice.entity.product.ImageProduct;
import com.example.productservice.entity.product.Product;
import com.example.productservice.exceptions.product.ImageNotFoundException;
import com.example.productservice.exceptions.product.ProductNotFoundException;
import com.example.productservice.service.file.MinioService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final MinioService minioService;
    private final ProductRepository productRepository;
    private final ImageProductRepository imageProductRepository;

    public void upload(MultipartFile[] images, Long productId) {

        log.info("Upload file for product -> {}", productId);

        List<String> imagePaths = Arrays.stream(images)
                .map(i -> minioService.uploadProductImages(i, productId))
                .toList();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        List<ImageProduct> imageProducts = imagePaths.stream()
                .map(path -> {

                    ImageProduct image = new ImageProduct(path);
                    image.setProduct(product);

                    return image;

                })
                .toList();

        imageProductRepository.saveAll(imageProducts);

        log.info("Uploaded file for product -> {}", productId);
    }

    public InputStream download(String imagePath) {
        return minioService.getObject(imagePath);
    }

}
