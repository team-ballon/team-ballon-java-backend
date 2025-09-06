package com.ballon.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "image_link")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ImageLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_link_id", nullable = false)
    private Long imageLinkId;

    @Column(nullable = false, length = 1000)
    private String link;

    @Column(nullable = false)
    private int order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
