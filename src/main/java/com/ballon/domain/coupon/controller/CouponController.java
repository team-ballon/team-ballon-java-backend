package com.ballon.domain.coupon.controller;

import com.ballon.domain.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "쿠폰 관련 API", description = "쿠폰과 관련된 기능")
public class CouponController {
    private final CouponService couponService;

    @PostMapping("/{coupon-id}/assign")
    public ResponseEntity<Void> assignCouponByUser(@PathVariable("coupon-id") Long couponId) {
        couponService.assignCouponByUser(couponId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
