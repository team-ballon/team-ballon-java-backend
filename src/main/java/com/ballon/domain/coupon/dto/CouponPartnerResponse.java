package com.ballon.domain.coupon.dto;


import com.ballon.domain.coupon.entity.type.Type;
import com.querydsl.core.annotations.QueryProjection;

public record CouponPartnerResponse(
        Long couponId,
        String couponName,
        String type,
        int discount,
        String partnerName
) {
    @QueryProjection
    public CouponPartnerResponse(Long couponId, String couponName, Type type, Integer discount, String partnerName) {
        this(couponId, couponName, type.toString(), discount, partnerName);
    }
}