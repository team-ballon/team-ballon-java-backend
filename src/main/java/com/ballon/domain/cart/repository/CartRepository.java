package com.ballon.domain.cart.repository;

import com.ballon.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserUserId(Long userId);
}