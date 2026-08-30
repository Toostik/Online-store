package com.example.productservice.controller.images;

import com.example.productservice.service.file.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class ImageController {

    private final MinioService minioService;

    @GetMapping("/products/{productId}/{fileName:.+}")
    public ResponseEntity<InputStreamResource> getImage(
            @PathVariable Long productId,
            @PathVariable String fileName) {

        String objectName = productId + "/" + fileName;

        InputStream stream = minioService.getObject(objectName);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(stream));
    }

}
