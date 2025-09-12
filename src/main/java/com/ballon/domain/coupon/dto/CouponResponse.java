package com.ballon.domain.coupon.dto;

import com.ballon.domain.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CouponResponse {
    private Long couponId;
    private String couponName;
    private int discount;
    private String discountType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static CouponResponse from(Coupon c) {
        return new CouponResponse(
                c.getCouponId(),
                c.getCouponName(),
                c.getDiscount(),
                c.getType().name(),
                c.getEvent().getStartDate(),
                c.getEvent().getEndDate()
        );
    }
}
