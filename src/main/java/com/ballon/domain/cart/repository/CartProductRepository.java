package com.ballon.domain.cart.repository;

import com.ballon.domain.cart.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    // cart_id + product_id (uk_cart_product) 조합으로 단일 라인 조회
    Optional<CartProduct> findByCartIdAndProductId(Long cartId, Long productId);
}
