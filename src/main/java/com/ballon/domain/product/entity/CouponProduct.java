package com.ballon.domain.product.entity;

import com.ballon.domain.coupon.entity.Coupon;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "coupon_product",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_coupon_product", columnNames = {"coupon_id", "product_id"})
        },
        indexes = {
                @Index(name = "idx_coupon_product_coupon", columnList = "coupon_id"),
                @Index(name = "idx_coupon_product_product", columnList = "product_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CouponProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_product_id", nullable = false)
    private Long couponProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;
}
