package com.example.cartservice.service.cart.command;

import com.example.cartservice.dao.cart.CartRepository;
import com.example.cartservice.dto.cart.CartItemDto;
import com.example.cartservice.entity.cart.Cart;
import com.example.cartservice.entity.cart.CartItem;
import com.example.cartservice.exceptions.cart.CartExistsException;
import com.example.cartservice.service.cart.cache.CartCacheService;
import com.example.cartservice.service.cart.event.CartOutboxService;
import com.example.cartservice.service.cart.helper.*;
import com.example.cartservice.service.security.SecurityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartCommandService {

    private final CartRepository cartRepository;

    private final SecurityService securityService;
    private final CartCacheService cartCacheService;

    private final CartCreatorService cartCreatorService;
    private final CartFinderService cartFinderService;

    private final CartItemUpdaterService cartItemUpdater;
    private final CartItemAdderService cartItemAdderService;
    private final CartItemRemoverService cartItemRemoverService;
    private final CartCleanerService cartCleanerService;
    private final CartDeletionService cartDeletionService;

    private final CartOutboxService cartOutboxService;

    public void createCart(List<CartItemDto> items) {

        Long userId = securityService.getCurrentUserId();

        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseGet(
                        () -> cartCreatorService.create(userId)
                );

        if (!cart.getItems().isEmpty()) {
            throw new CartExistsException();
        }

        if (items != null && !items.isEmpty()) {
            cartItemAdderService.addItems(cart, items);
        }

        cartCacheService.delete(userId);

        cartOutboxService.publishCreated(cart);
    }

    public void addItems(List<CartItemDto> items) {

        Long userId = securityService.getCurrentUserId();

        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseGet(() -> cartCreatorService.create(userId));

        cartItemAdderService.addItems(cart, items);

        cartCacheService.delete(userId);

        cartOutboxService.publishItemsAdded(
                cart,
                items.stream()
                        .map(CartItemDto::getProductId)
                        .toList()
        );

    }

    public void clearCart() {

        Long userId = securityService.getCurrentUserId();

        Cart cart = cartFinderService.findByUserId(userId);

        cartCleanerService.clear(cart);

        cartCacheService.delete(userId);

        cartOutboxService.publishCleared(cart);

    }

    public void deleteCart() {

        Long userId = securityService.getCurrentUserId();

        Cart cart = cartFinderService.findByUserId(userId);

        cartDeletionService.delete(cart);

        cartCacheService.delete(userId);

        cartOutboxService.publishDeleted(cart.getId());

    }

    public void deleteItem(Long itemId) {

        Long userId = cartItemRemoverService.remove(itemId);

        cartCacheService.delete(userId);
    }

    public void updateQuantity(Long id, Integer quantity) {

        if (quantity <= 0) {
            deleteItem(id);
            return;
        }

        Long userId = cartItemUpdater.updateQuantity(
                id,
                quantity
        );

        cartCacheService.delete(userId);

    }
}