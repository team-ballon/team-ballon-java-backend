package com.ballon.domain.user.entity;

import com.ballon.domain.coupon.entity.Coupon;
import com.ballon.domain.user.entity.id.UserCouponId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserCoupon {
    @EmbeddedId
    private UserCouponId userCouponId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("couponId")
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(nullable = false)
    private Boolean isUsed;

    private LocalDateTime usedAt;

    public static UserCoupon createUserCoupon(User user, Coupon coupon) {
        return UserCoupon.builder()
                .userCouponId(new UserCouponId(user.getUserId(), coupon.getCouponId()))
                .user(user)
                .coupon(coupon)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCoupon that)) return false;
        return Objects.equals(userCouponId, that.userCouponId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userCouponId);
    }

    @PrePersist
    public void prePersist() {
        this.isUsed = Boolean.FALSE;
    }
}
