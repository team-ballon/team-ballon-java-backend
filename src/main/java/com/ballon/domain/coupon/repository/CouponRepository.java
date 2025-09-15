package com.ballon.domain.coupon.repository;

import com.ballon.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CustomCouponRepository {
    @Query("select c from Coupon c join fetch c.event where c.couponId in :ids")
    List<Coupon> findAllByIdWithEvent(@Param("ids") List<Long> ids);
}
