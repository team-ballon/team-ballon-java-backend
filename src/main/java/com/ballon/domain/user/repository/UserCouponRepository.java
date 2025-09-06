package com.ballon.domain.user.repository;

import com.ballon.domain.user.entity.UserCoupon;
import com.ballon.domain.user.entity.id.UserCouponId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserCouponRepository extends CrudRepository<UserCoupon, UserCouponId> {
    @Modifying
    @Query("UPDATE UserCoupon uc " +
            "SET uc.isUsed = true " +
            "WHERE uc.user.userId = :userId AND uc.coupon.couponId IN :couponIds")
    int markCouponsAsUsed(@Param("userId") Long userId, @Param("couponIds") List<Long> couponIds);

    // 회원의 "사용 가능" 쿠폰 조회 (기간 = event 기준)
    @Query("""
    select uc
    from UserCoupon uc
    join fetch uc.coupon c
    join fetch c.event e
    where uc.user.userId = :userId
      and uc.isUsed = false
      and e.startDate <= :now and e.endDate >= :now
    order by e.endDate asc
    """)
    List<UserCoupon> findUsableByUser(@Param("userId") Long userId,
                                      @Param("now") LocalDateTime now);

}
