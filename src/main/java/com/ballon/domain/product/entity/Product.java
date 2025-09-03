package com.ballon.domain.product.entity;

import com.ballon.domain.category.entity.Category;
import com.ballon.domain.partner.entity.Partner;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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