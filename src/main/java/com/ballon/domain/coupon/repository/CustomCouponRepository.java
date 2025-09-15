package com.ballon.domain.coupon.repository;

import com.ballon.domain.coupon.dto.CouponPartnerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomCouponRepository {
    Page<CouponPartnerResponse> findCouponsByEventId(Long eventId, Pageable pageable);
}
