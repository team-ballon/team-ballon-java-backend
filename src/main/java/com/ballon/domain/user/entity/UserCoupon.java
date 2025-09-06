package com.ballon.domain.user.entity;

import com.ballon.domain.admin.entity.Admin;
import com.ballon.domain.admin.entity.AdminPermission;
import com.ballon.domain.admin.entity.Permission;
import com.ballon.domain.admin.entity.id.AdminPermissionId;
import com.ballon.domain.coupon.entity.Coupon;
import com.ballon.domain.user.entity.id.UserCouponId;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "user_coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
