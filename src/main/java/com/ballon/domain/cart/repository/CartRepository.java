package com.ballon.domain.cart.repository;

import com.ballon.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // 카트가 유저당 1개라면
    Optional<Cart> findByUserUserId(Long userId);

    // 카트가 여러개 일 수도 있으면 인데 가능성없으니 주석처리
   // List<Cart> findByUserUserId(Long userId);
}
