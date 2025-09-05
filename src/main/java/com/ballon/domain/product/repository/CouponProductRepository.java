package com.ballon.domain.product.repository;

import com.ballon.domain.product.entity.CouponProduct;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CouponProductRepository extends CrudRepository<CouponProduct, Long> {
    Optional<CouponProduct> findByProduct_IdAndCoupon_CouponId(Long productId, Long couponId);
}
