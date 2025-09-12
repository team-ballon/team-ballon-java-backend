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

    @Query("""
    select uc
    from UserCoupon uc
    join fetch uc.coupon c
    join fetch c.event e
    join CouponProduct cp on cp.coupon.couponId = c.couponId
    where uc.user.userId = :userId
      and uc.isUsed = false
      and e.startDate <= :now and e.endDate >= :now
      and cp.product.id = :productId
    order by e.endDate asc
    """)
    List<UserCoupon> findUsableByUserAndProduct(@Param("userId") Long userId,
                                                @Param("productId") Long productId,
                                                @Param("now") LocalDateTime now);
}
