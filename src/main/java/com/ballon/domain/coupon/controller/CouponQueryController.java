package com.ballon.domain.coupon.controller;


import com.ballon.domain.coupon.dto.CouponResponse;
import com.ballon.domain.coupon.service.CouponQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CouponQueryController {

    private final CouponQueryService couponQueryService;

    // 회원 기준: 지금 사용할 수 있는 쿠폰
    // 예) GET /api/users/10/coupons/usable
    @GetMapping("/users/{userId}/coupons/usable")
    public List<CouponResponse> getUsableByUser(@PathVariable Long userId) {
        return couponQueryService.getUsableCouponsByUser(userId);
    }

    // 상품 기준: 지금 적용 가능한 쿠폰
    // 예) GET /api/products/100/coupons/usable
    @GetMapping("/products/{productId}/coupons/usable")
    public List<CouponResponse> getUsableByProduct(@PathVariable Long productId) {
        return couponQueryService.getUsableCouponsByProduct(productId);
    }
}
