package com.ballon.domain.coupon.repository.impl;

import com.ballon.domain.coupon.dto.CouponPartnerResponse;
import com.ballon.domain.coupon.dto.QCouponPartnerResponse;
import com.ballon.domain.coupon.entity.QCoupon;
import com.ballon.domain.coupon.repository.CustomCouponRepository;
import com.ballon.domain.event.entity.QEvent;
import com.ballon.domain.partner.entity.QPartner;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomCouponRepositoryImpl implements CustomCouponRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CouponPartnerResponse> findCouponsByEventId(Long eventId, Pageable pageable) {
        QCoupon coupon = QCoupon.coupon;
        QPartner partner = QPartner.partner;
        QEvent event = QEvent.event;

        // 데이터 조회
        List<CouponPartnerResponse> content = queryFactory
                .select(new QCouponPartnerResponse(
                        coupon.couponId,
                        coupon.couponName,
                        coupon.type,
                        coupon.discount,
                        partner.partnerName))
                .from(coupon)
                .join(coupon.partner, partner)
                .join(coupon.event, event)
                .where(event.eventId.eq(eventId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(coupon.couponId.desc()) // 정렬 예시
                .fetch();

        // 카운트 쿼리
        Long count = queryFactory
                .select(coupon.count())
                .from(coupon)
                .join(coupon.event, event)
                .where(event.eventId.eq(eventId))
                .fetchOne();

        return new PageImpl<>(content, pageable, count != null ? count : 0);
    }
}