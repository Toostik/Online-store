package com.example.orderservice.service.order.query;

import com.example.orderservice.dao.order.OrderItemRepository;
import com.example.orderservice.dao.order.OrderRepository;
import com.example.orderservice.dto.order.ProfileOrders;
import com.example.orderservice.dto.order.RecentOrderItemDto;
import com.example.orderservice.dto.product.ProfileProducts;
import com.example.orderservice.entity.order.OrderItem;
import com.example.orderservice.service.integration.ProductService;
import com.example.orderservice.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RecentOrderService {

    private final SecurityService securityService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;

    public ProfileOrders getRecentItems(Integer size) {

        if (size == null) {
            size = 5;
        }

        Long userId = securityService.getCurrentUserId();

        List<OrderItem> items =
                orderItemRepository.findRecentItems(
                        userId,
                        PageRequest.of(0, size)
                );

        Map<Long, OrderItem> itemMap =
                items.stream()
                        .collect(Collectors.toMap(
                                OrderItem::getProductId,
                                Function.identity(),
                                (oldItem, newItem) -> oldItem,
                                LinkedHashMap::new
                        ));

        List<Long> ids = itemMap.keySet()
                .stream()
                .toList();

        ProfileProducts profileProducts =
                productService.getProductsById(ids);

        List<RecentOrderItemDto> recentItems =
                profileProducts.productDtoList()
                        .stream()
                        .map(product -> {

                            OrderItem item =
                                    itemMap.get(product.id());

                            String image = product.imageUrls().isEmpty()
                                    ? null
                                    : product.imageUrls().getFirst();

                            return new RecentOrderItemDto(
                                    product.id(),
                                    product.name(),
                                    image,
                                    item.getQuantity(),
                                    product.price(),
                                    item.getStatus(),
                                    item.getOrder().getCreatedAt()
                            );

                        })
                        .toList();

        log.info(
                "User get recent order items -> {}",
                userId
        );

        return new ProfileOrders(
                recentItems,
                orderRepository.countOrderByUserId(userId),
                profileProducts.totalWishlistItems()
        );
    }

}
