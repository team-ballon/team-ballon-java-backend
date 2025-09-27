package com.ballon.domain.product.entity;

import com.ballon.domain.category.entity.Category;
import com.ballon.domain.partner.entity.Partner;
import com.ballon.domain.product.entity.type.ProductApplicationStatus;
import com.ballon.domain.product.entity.type.ProductApplicationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_application")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProductApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_application_id", nullable = false)
    private Long productApplicationId;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    private ProductApplicationStatus status;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    private ProductApplicationType type;

    private LocalDateTime applicationDate;

    private int minQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private Partner partner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @PrePersist
    public void prePersist() {
        this.applicationDate = LocalDateTime.now();
    }
}
