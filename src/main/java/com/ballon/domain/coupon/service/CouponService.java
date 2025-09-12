package com.ballon.domain.coupon.service;

import com.ballon.domain.coupon.dto.UserCouponResponse;

import java.util.List;

public interface CouponService {
    void assignCouponByUser(Long couponId);

    List<UserCouponResponse> getUsableCouponsByUser();

    List<UserCouponResponse> findUsableByUserAndProduct(Long productId);
}
