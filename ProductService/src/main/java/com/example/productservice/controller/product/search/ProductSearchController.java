package com.example.productservice.controller.product.search;

import com.example.productservice.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductService productService;

//ElasticSearch
//
//    @PostMapping("/reindex")
//    public void reindex(){
//        productService.reindexAll();
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<List<ProductDocument>> search(
//            @RequestParam(required = false) String query,
//            @RequestParam(required = false) Long categoryId,
//            @RequestParam(required = false) BigDecimal minPrice,
//            @RequestParam(required = false) BigDecimal maxPrice,
//            @RequestParam(required = false) String sort
//    ) {
//
//        SearchRequest request = new SearchRequest(
//                query, categoryId, minPrice, maxPrice, sort
//        );
//
//        log.info("GET /api/products/search - query={}, categoryId={}, minPrice={}, maxPrice={}",
//                query, categoryId, minPrice, maxPrice);
//
//        return ResponseEntity.ok(productService.search(request));
//    }
//
//    @GetMapping("/autocomplete")
//    public ResponseEntity<List<String>> autocomplete(
//            @RequestParam String prefix
//    ) {
//        return ResponseEntity.ok(productService.autocomplete(prefix));
//    }

}
