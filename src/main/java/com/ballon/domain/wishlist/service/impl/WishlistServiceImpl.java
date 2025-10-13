package com.ballon.domain.wishlist.service.impl;

import com.ballon.domain.product.entity.Product;
import com.ballon.domain.product.repository.ProductRepository;
import com.ballon.domain.user.entity.User;
import com.ballon.domain.user.repository.UserRepository;
import com.ballon.domain.wishlist.dto.WishlistResponse;
import com.ballon.domain.wishlist.entity.Wishlist;
import com.ballon.domain.wishlist.entity.id.WishlistId;
import com.ballon.domain.wishlist.repository.WishlistRepository;
import com.ballon.domain.wishlist.service.WishlistService;
import com.ballon.global.UserUtil;
import com.ballon.global.common.exception.ForbiddenException;
import com.ballon.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public void addWishlist(Long productId) {
        Long userId = UserUtil.getUserId();
        WishlistId wishlistId = new WishlistId(userId, productId);

        log.info("addWishlist 호출 - userId: {}, productId: {}", userId, productId);

        if(wishlistRepository.existsById(wishlistId)) {
            throw new ForbiddenException("이미 존재하는 wishlist 입니다.");
        }

        User user = userRepository.getReferenceById(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));

        Wishlist wishlist = new Wishlist(wishlistId, user, product);
        wishlistRepository.save(wishlist);

        log.info("wishlist 추가 성공 - userId: {}, productId: {}", userId, productId);
    }

    @Override
    public void removeWishlist(Long productId) {
        Long userId = UserUtil.getUserId();
        WishlistId wishlistId = new WishlistId(userId, productId);

        log.info("removeWishlist 호출 - userId: {}, productId: {}", userId, productId);

        wishlistRepository.deleteById(wishlistId);
        log.info("wishlist 삭제 성공 - userId: {}, productId: {}", userId, productId);
    }

    @Override
    public List<WishlistResponse> getWishlists() {
        Long userId = UserUtil.getUserId();
        log.info("getWishlists 호출 - userId: {}", userId);

        List<WishlistResponse> wishlists = wishlistRepository.findProductsByUserId(userId);

        log.info("getWishlists 결과 수 - userId: {}, count: {}", userId, wishlists.size());
        return wishlists;
    }
}