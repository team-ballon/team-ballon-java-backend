package com.ballon.domain.cart.repository;

import com.ballon.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndCartUserUserId(Long cartId, Long userId);

    //카트 여러개일때 사용가능 한건데 쓸일 없어보여 주석처리
//    List<CartItem> findAllByCartUserUserId(Long userId);
//    Optional<CartItem> findByCartUserUserIdAndProductId(Long userId, Long productId);
}
