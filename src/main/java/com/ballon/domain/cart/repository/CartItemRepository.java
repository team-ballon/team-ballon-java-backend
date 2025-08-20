package com.ballon.domain.cart.repository;

import com.ballon.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndUserId(Long cartId, Long productId);
}
