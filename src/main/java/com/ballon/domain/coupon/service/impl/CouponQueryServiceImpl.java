package com.ballon.domain.coupon.service.impl;

import com.ballon.domain.coupon.dto.CouponResponse;
import com.ballon.domain.user.entity.UserCoupon;
import com.ballon.domain.coupon.repository.CouponRepository;
import com.ballon.domain.coupon.service.CouponQueryService;
import com.ballon.domain.user.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponQueryServiceImpl implements CouponQueryService {


    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;


    @Override
    public List<CouponResponse> getUsableCouponsByUser(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<UserCoupon> rows = userCouponRepository.findUsableByUser(userId, now);
        return rows.stream()
                .map(uc -> CouponResponse.from(uc.getCoupon()))
                .toList();
    }

    @Override
    public List<CouponResponse> getUsableCouponsByProduct(Long productId) {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findUsableByProduct(productId, now);
    }
}
