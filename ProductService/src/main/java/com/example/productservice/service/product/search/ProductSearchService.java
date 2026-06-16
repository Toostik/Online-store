package com.example.productservice.service.product.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
//@Service
@RequiredArgsConstructor
@Transactional
public class ProductSearchService {

//    public List<ProductDocument> search(SearchRequest request) {
//        log.info("Searching products: {}", request);
//        return productSearchRepository.search(request);
//    }
//
//    public List<String> autocomplete(String prefix) {
//        return productSearchRepository.autocomplete(prefix);
//    }
//
//    public void reindexAll() {
//        log.warn("Reindexing all products in Elasticsearch");
//
//        List<Product> products = (List<Product>) productRepository.findAll();
//
//        List<ProductDocument> docs = products.stream()
//                .map(Product::toDoc).toList();
//
//        productSearchRepository.saveAll(docs);
//
//    }

}
