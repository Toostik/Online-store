package com.example.productservice.dao.elastic;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductSearchCustomRepositoryImpl
        implements ProductSearchCustomRepository {
//
//    private final ElasticsearchOperations elasticsearchOperations;
//
//    @Override
//    public List<ProductDocument> search(SearchRequest request) {
//
//        Query query = Query.of(q -> q.bool(b -> {
//
//            // FULL TEXT
//            if (request.getQuery() != null && !request.getQuery().isBlank()) {
//
//                b.must(m -> m.bool(bb -> {
//
//                    bb.should(s -> s.multiMatch(mm -> mm
//                            .query(request.getQuery())
//                            .fields("name^2", "description")
//                            .fuzziness("AUTO")
//                    ));
//
//                    // 2. prefix
//                    bb.should(s -> s.matchPhrasePrefix(mp -> mp
//                            .field("name")
//                            .query(request.getQuery())
//                    ));
//
//                    // wildcard
//                    bb.should(s -> s.wildcard(w -> w
//                            .field("name")
//                            .value("*" + request.getQuery().toLowerCase() + "*")
//                    ));
//
//                    bb.minimumShouldMatch("1");
//
//                    return bb;
//                }));
//            }
//
//            // CATEGORY
//            if (request.getCategoryId() != null) {
//                b.filter(f -> f.term(t -> t
//                        .field("categoryId")
//                        .value(request.getCategoryId())
//                ));
//            }
//
//            // PRICE
//            if (request.getMinPrice() != null || request.getMaxPrice() != null) {
//                b.filter(f -> f.range(r -> r.number(n -> {
//
//                    n.field("price");
//
//                    if (request.getMinPrice() != null) {
//                        n.gte(request.getMinPrice().doubleValue());
//                    }
//
//                    if (request.getMaxPrice() != null) {
//                        n.lte(request.getMaxPrice().doubleValue());
//                    }
//
//                    return n;
//                })));
//            }
//
//            return b;
//        }));
//
//        NativeQuery nativeQuery = NativeQuery.builder()
//                .withQuery(query)
//                .withPageable(PageRequest.of(0, 10))
//                .build();
//
//        // SORT
//        if ("price_asc".equals(request.getSort())) {
//            nativeQuery.addSort(Sort.by(Sort.Direction.ASC, "price"));
//        } else if ("price_desc".equals(request.getSort())) {
//            nativeQuery.addSort(Sort.by(Sort.Direction.DESC, "price"));
//        }
//
//        SearchHits<ProductDocument> hits =
//                elasticsearchOperations.search(nativeQuery, ProductDocument.class);
//
//        return hits.stream()
//                .map(SearchHit::getContent)
//                .toList();
//    }
//
//    @Override
//    public List<String> autocomplete(String prefix) {
//
//        Query query = Query.of(q -> q
//                .matchPhrasePrefix(m -> m
//                        .field("name")
//                        .query(prefix)
//                )
//        );
//
//        NativeQuery nativeQuery = NativeQuery.builder()
//                .withQuery(query)
//                .withPageable(PageRequest.of(0, 5))
//                .build();
//
//        SearchHits<ProductDocument> hits =
//                elasticsearchOperations.search(nativeQuery, ProductDocument.class);
//
//        return hits.stream()
//                .map(hit -> hit.getContent().getName())
//                .toList();
//    }
}