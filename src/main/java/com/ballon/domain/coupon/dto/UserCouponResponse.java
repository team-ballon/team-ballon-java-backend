package com.ballon.domain.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCouponResponse {
    private Long couponId;
    private String couponName;
    private int discount;
    private String discountType;
}
