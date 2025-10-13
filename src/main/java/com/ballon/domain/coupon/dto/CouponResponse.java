package com.ballon.domain.coupon.dto;

import com.querydsl.core.annotations.QueryProjection;
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
    private String discountType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @QueryProjection
    public CouponResponse(Long couponId, String couponName, Integer discountValue, String  discountType, LocalDateTime startDate, LocalDateTime endDate) {
        this.couponId = couponId;
        this.couponName = couponName;
        this.discount = discountValue;
        this.discountType = discountType;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
