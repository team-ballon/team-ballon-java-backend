package com.ballon.domain.coupon.entity;

import com.ballon.domain.coupon.entity.type.Type;
import com.ballon.domain.event.entity.Event;
import com.ballon.domain.partner.entity.Partner;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    private String couponName;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Integer discountValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;
}
