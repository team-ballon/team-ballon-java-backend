package com.ballon.domain.product.entity;

import com.ballon.domain.category.entity.Category;
import com.ballon.domain.partner.entity.Partner;
import jakarta.persistence.*;
import lombok.*;

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
    public enum Status {ACTIVE, INACTIVE, OUT_OF_STOCK}

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer price;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer quantity;

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

    //도메인 변경시 사용 (검증포함) (사용은 서비스 트렌젝셔널에서 사용)
    public void changeCategory(Category category) {
        if (category == null) throw new IllegalArgumentException("카테고리는 null일 수 없습니다.");
        this.category = category;
    }
    public void changePrice(Integer price) {
        if (price < 0) throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        this.price = price;
    }
    public void changeQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) throw new IllegalArgumentException("수량은 0 이상이어야 합니다.");
        this.quantity = quantity;
    }
    public void changeStatus(Status status) {
        if (status == null) throw new IllegalArgumentException("상태는 null일 수 없습니다.");
        this.status = status;
    }




}
