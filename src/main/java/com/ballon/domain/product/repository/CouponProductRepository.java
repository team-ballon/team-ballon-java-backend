package com.ballon.domain.product.repository;

import com.ballon.domain.product.entity.CouponProduct;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CouponProductRepository extends CrudRepository<CouponProduct, Long> {
    Optional<CouponProduct> findByProduct_IdAndCoupon_CouponId(Long productId, Long couponId);

    @Query("select cp.coupon.couponId from CouponProduct cp where cp.product.id = :productId")
    List<Long> findCouponIdsByProductId(@Param("productId") Long productId);
}
