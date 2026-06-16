package com.example.cartservice.service.cart.builder;

import com.example.cartservice.dto.cart.CartItemResponse;
import com.example.cartservice.dto.cart.CartResponse;
import com.example.cartservice.dto.product.ProductDto;
import com.example.cartservice.entity.cart.Cart;
import com.example.cartservice.entity.cart.CartItem;
import com.example.cartservice.service.integration.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartResponseBuilder {

    private final ProductService productService;

    public CartResponse build(Cart cart) {

        List<Long> productIds =
                cart.getItems()
                        .stream()
                        .map(CartItem::getProductId)
                        .toList();

        Map<Long, ProductDto> products =
                productService.getProducts(productIds);

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal shippingCost = new BigDecimal("495");

        List<CartItemResponse> responses = new ArrayList<>();

        for (CartItem item : cart.getItems()) {

            ProductDto product =
                    products.get(item.getProductId());

            BigDecimal itemTotal =
                    item.getPriceAtAddTime()
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getQuantity()
                                    )
                            );

            subtotal = subtotal.add(itemTotal);

            responses.add(

                    new CartItemResponse(
                            item.getProductId(),
                            product.name(),
                            product.brand(),
                            product.sku(),
                            product.imageUrls().getFirst(),
                            item.getPriceAtAddTime(),
                            item.getQuantity(),
                            itemTotal
                    )

            );

        }

        return CartResponse.builder()
                .cartId(cart.getId())
                .itemsCount(cart.getItems().size())
                .subtotal(subtotal)
                .discount(discount)
                .shippingCost(shippingCost)
                .totalPrice(
                        subtotal
                                .subtract(discount)
                                .add(shippingCost)
                )
                .items(responses)
                .build();
    }

}
