package com.ballon.domain.coupon.service;

import com.ballon.domain.coupon.dto.CouponResponse;

import java.util.List;

public interface CouponQueryService {
    List<CouponResponse> getUsableCouponsByUser(Long userId);
    List<CouponResponse> getUsableCouponsByProduct(Long productId);
}
