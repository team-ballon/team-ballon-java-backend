package com.ballon.domain.user.repository;

import com.ballon.domain.user.entity.UserCoupon;
import com.ballon.domain.user.entity.id.UserCouponId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCouponRepository extends CrudRepository<UserCoupon, UserCouponId> {
    @Modifying
    @Query("UPDATE UserCoupon uc " +
            "SET uc.isUsed = true " +
            "WHERE uc.user.userId = :userId AND uc.coupon.couponId IN :couponIds")
    int markCouponsAsUsed(@Param("userId") Long userId, @Param("couponIds") List<Long> couponIds);
}
