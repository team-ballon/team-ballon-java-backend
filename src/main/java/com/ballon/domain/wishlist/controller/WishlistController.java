package com.ballon.domain.wishlist.controller;

import com.ballon.domain.wishlist.dto.WishlistResponse;
import com.ballon.domain.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Tag(name = "찜 목록 API", description = "찜 목록과 관련된 기능")
public class WishlistController {
    private final WishlistService wishlistService;

    @GetMapping("/")
    public ResponseEntity<List<WishlistResponse>> getWishlists() {
        return  ResponseEntity.ok(wishlistService.getWishlists());
    }

    @PostMapping("/")
    public ResponseEntity<Void> addWishlist(Long productId) {
        wishlistService.addWishlist(productId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
