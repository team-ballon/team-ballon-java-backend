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
    private Double avgRating;
    private Long reviewCount;

    @Override
    public String toString() {
        return "ProductSearchResponse{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", avgRating=" + avgRating +
                ", reviewCount=" + reviewCount +
                ", productId=" + productId +
                '}';
    }
}