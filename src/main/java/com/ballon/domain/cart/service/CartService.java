package com.ballon.domain.cart.service;

import com.ballon.domain.cart.dto.CartProductRequest;
import com.ballon.domain.cart.dto.CartResponse;
import com.ballon.domain.cart.dto.SelectedCartProduct;

import java.util.List;

public interface CartService {
    CartResponse getMyCart(Long userId);
    CartResponse addProduct(Long userId, CartProductRequest req);
    CartResponse changeQuantity(Long userId, Long cartProductId, Integer quantity);
    CartResponse removeProduct(Long userId, Long cartProductId);
    void removeSelectedProducts(Long userId, List<SelectedCartProduct> cartProductIds);
}