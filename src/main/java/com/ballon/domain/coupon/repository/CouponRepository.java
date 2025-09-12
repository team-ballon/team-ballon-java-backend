package com.ballon.domain.coupon.repository;

import com.ballon.domain.coupon.dto.CouponResponse;
import com.ballon.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Query("select c from Coupon c join fetch c.event where c.couponId in :ids")
    List<Coupon> findAllByIdWithEvent(@Param("ids") List<Long> ids);

    @Query("""
        select new com.ballon.domain.coupon.dto.CouponResponse(
            c.couponId,
            c.couponName,
            c.discount,
            cast(c.type as string),
            e.startDate,
            e.endDate
        )
        from Coupon c
        join c.event e
        where exists (
            select 1
            from com.ballon.domain.product.entity.CouponProduct cp
            where cp.coupon = c
              and cp.product.id = :productId
        )
          and e.startDate <= :now and e.endDate >= :now
        order by e.endDate asc
        """)
    List<CouponResponse> findUsableByProduct(
            @Param("productId") Long productId,
            @Param("now") LocalDateTime now
    );
}
