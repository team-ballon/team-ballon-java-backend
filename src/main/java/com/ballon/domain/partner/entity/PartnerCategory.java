package com.ballon.domain.partner.entity;

import com.ballon.domain.category.entity.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "partner_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PartnerCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_category_id")
    private Long partnerCategoryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partner_id",
            foreignKey = @ForeignKey(name = "fk_pc_partner"))
    private Partner partner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id",
            foreignKey = @ForeignKey(name = "fk_pc_category"))
    private Category category;

    public static PartnerCategory of(Partner partner, Category category) {
        return PartnerCategory.builder()
                .partner(partner)
                .category(category)
                .build();
    }
}

