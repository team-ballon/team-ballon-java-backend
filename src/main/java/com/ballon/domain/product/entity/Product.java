package com.ballon.domain.product.entity;

import com.ballon.domain.category.entity.Category;
import com.ballon.domain.partner.entity.Partner;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product",
        indexes = {
                @Index(name = "idx_product_category", columnList = "category_id"),
                @Index(name = "idx_product_price", columnList = "price"),
                @Index(name = "idx_product_partner", columnList = "partner_id")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long id;

    @Column(length = 1000)
    private String productUrl;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_category"))
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_partner"))
    private Partner partner;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CouponProduct> adminPermissions = new HashSet<>();

    public static Product createProduct(String productUrl, String name, Integer price, Integer quantity) {
        return Product.builder()
                .productUrl(productUrl)
                .name(name)
                .price(price)
                .quantity(quantity)
                .build();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}