package com.ballon.domain.coupon.dto;

import com.ballon.domain.coupon.entity.type.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
public class CouponResponse {
    private Long couponId;
    private String couponName;
    private int discount;
    private Type discountType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
