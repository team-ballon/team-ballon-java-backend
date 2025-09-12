package com.ballon.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductSearchResponse {
    private Long productId;
    private String productUrl;
    private String name;
    private Integer price;
    private Long partnerId;
    private String partnerName;
    private Double avgRating;
    private Long reviewCount;
}