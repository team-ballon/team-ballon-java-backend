package com.ballon.domain.coupon.dto;

import com.ballon.domain.coupon.entity.Coupon;
import com.ballon.domain.coupon.entity.type.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CouponResponse {
    private Long couponId;
    private String couponName;

    private Type type;

    private Integer discountAmount;


    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static CouponResponse from(Coupon c) {
        return CouponResponse.builder()
                .couponId(c.getCouponId())
                .couponName(c.getCouponName())
                .type(c.getType())
                .discountAmount(c.getDiscount())
                .startDate(c.getEvent().getStartDate())
                .endDate(c.getEvent().getEndDate())
                .build();
    }

}

