package com.ballon.domain.order.entity;

import com.ballon.domain.coupon.entity.Coupon;
import com.ballon.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_id", nullable = false)
    private Long orderProductId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int productAmount;

    @Column(nullable = false)
    private int discountAmount;

    @Column(nullable = false)
    private int paidAmount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    public static OrderProduct createOrderProduct(Product product, Coupon coupon,int quantity, int productAmount, int discountAmount, int paidAmount) {
        return OrderProduct.builder()
                .product(product)
                .coupon(coupon)
                .quantity(quantity)
                .productAmount(productAmount)
                .discountAmount(discountAmount)
                .paidAmount(paidAmount)
                .build();
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
