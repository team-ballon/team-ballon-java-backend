package com.ballon.domain.coupon.repository;

import com.ballon.domain.coupon.dto.CouponResponse;
import com.ballon.domain.coupon.entity.CouponProduct;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CouponProductRepository extends CrudRepository<CouponProduct, Long> {
    Optional<CouponProduct> findByProduct_IdAndCoupon_CouponId(Long productId, Long couponId);

    @Query("""
    SELECT new com.ballon.domain.coupon.dto.CouponResponse(
        c.couponId,
        c.couponName,
        c.discountValue,
        c.type,
        e.startDate,
        e.endDate
    )
    FROM CouponProduct cp
    JOIN cp.coupon c
    JOIN c.event e
    WHERE cp.product.id = :productId
""")
    List<CouponResponse> findCouponsByProductId(@Param("productId") Long productId);
}
