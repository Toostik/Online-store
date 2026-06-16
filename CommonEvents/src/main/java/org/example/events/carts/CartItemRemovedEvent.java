package org.example.events.carts;

import lombok.Builder;

@Builder
public record CartItemRemovedEvent(
        Long cartId,
        Long userId,
        Long itemId

) {
}