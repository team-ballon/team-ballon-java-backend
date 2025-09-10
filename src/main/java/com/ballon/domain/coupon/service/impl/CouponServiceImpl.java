package com.ballon.domain.coupon.service.impl;

import com.ballon.domain.coupon.dto.CouponResponse;
import com.ballon.domain.coupon.entity.Coupon;
import com.ballon.domain.coupon.repository.CouponRepository;
import com.ballon.domain.coupon.service.CouponService;
import com.ballon.domain.user.entity.User;
import com.ballon.domain.user.entity.UserCoupon;
import com.ballon.domain.user.repository.UserCouponRepository;
import com.ballon.domain.user.repository.UserRepository;
import com.ballon.global.UserUtil;
import com.ballon.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    @Override
    public void assignCouponByUser(Long couponId) {
        Long userId = UserUtil.getUserId();

        User user = userRepository.getReferenceById(userId);
        if(!couponRepository.existsById(couponId)) {
            throw new NotFoundException("존재하지 않는 쿠폰입니다.");
        }
        Coupon coupon = couponRepository.getReferenceById(couponId);

        userCouponRepository.save(UserCoupon.createUserCoupon(user, coupon));
    }
}
