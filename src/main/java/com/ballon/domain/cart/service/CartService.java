package com.ballon.domain.cart.service;

import com.ballon.domain.cart.dto.CartItemRequest;
import com.ballon.domain.cart.dto.CartResponse;

public interface CartService {
    CartResponse getMyCart(Long userId);
    CartResponse addItem(Long userId, CartItemRequest req);
    CartResponse changeQuantity(Long userId, Long cartItemId, Integer quantity);
    CartResponse removeItem(Long userId, Long cartItemId);
}
