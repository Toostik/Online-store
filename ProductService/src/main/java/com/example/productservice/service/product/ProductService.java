package com.example.productservice.service.product;

import com.example.productservice.dto.product.ProductDto;
import com.example.productservice.dto.product.ProfileProducts;
import com.example.productservice.dto.product.request.CheckProductRequest;
import com.example.productservice.dto.product.request.CheckProductResponse;
import com.example.productservice.dto.product.request.CreateProductRequest;
import com.example.productservice.dto.product.request.UpdateProductRequest;
import com.example.productservice.service.product.command.ProductCommandService;
import com.example.productservice.service.product.image.ProductImageService;
import com.example.productservice.service.inventory.InventoryService;
import com.example.productservice.service.product.query.ProductQueryService;
import com.example.productservice.service.product.wishlist.WishlistService;
import lombok.RequiredArgsConstructor;
import org.example.events.inventory.InventoryReleaseRequestedEvent;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductCommandService commandService;
    private final ProductQueryService queryService;
    private final ProductImageService imageService;
    private final InventoryService inventoryService;
    private final WishlistService wishlistService;

    public ProductDto createProduct(CreateProductRequest request) {
        return commandService.createProduct(request);
    }

    public void updateProduct(Long id, UpdateProductRequest request) {
        commandService.updateProduct(id, request);
    }

    public void deleteProduct(Long id) {
        commandService.deleteProduct(id);
    }

    public ProductDto getProductById(Long id) {
        return queryService.getProductById(id);
    }

    public ProfileProducts getProductsByIds(List<Long> ids){
        return queryService.getProductsByIds(ids);
    }

    public Page<ProductDto> getPageProducts(int page, int size) {
        return queryService.getPageProducts(page, size);
    }

    public List<ProductDto> getProductsWithImages(int page, int size){
        return queryService.getListProductsWithImages(page,size);
    }

    public void uploadImages(MultipartFile[] images, Long productId) {
        imageService.upload(images, productId);
    }


    public void putInWishlist(Long productId){
        wishlistService.putInWishlist(productId);
    }

    public CheckProductResponse getAvailability(CheckProductRequest request){
        return inventoryService.getProductAvailability(request);
    }

    public Boolean isProductExists(List<Long> ids){
        return inventoryService.isProductExists(ids);
    }

    public Map<Long, BigDecimal> getPrices(List<Long> ids){
    return queryService.getPrices(ids);
    }

    public BigDecimal getPriceById(Long id){
        return queryService.getPriceById(id);
    }

    public void releaseInventory(
            InventoryReleaseRequestedEvent event
    ) {

        inventoryService.releaseInventory(event);

    }
}
