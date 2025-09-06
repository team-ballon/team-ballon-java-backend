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
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static CouponResponse from(Coupon c) {
        return new CouponResponse(
                c.getCouponId(),
                c.getCouponName(),
                c.getEvent().getStartDate(),
                c.getEvent().getEndDate()
        );
    }
}

