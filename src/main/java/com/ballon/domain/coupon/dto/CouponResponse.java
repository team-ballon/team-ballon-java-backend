package com.ballon.domain.coupon.dto;

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
}
