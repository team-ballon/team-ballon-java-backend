package com.ballon.domain.wishlist.service;

import com.ballon.domain.wishlist.dto.WishlistResponse;

import java.util.List;

public interface WishlistService {
    void addWishlist(Long productId);

    void removeWishlist(Long productId);

    List<WishlistResponse> getWishlists();
}
