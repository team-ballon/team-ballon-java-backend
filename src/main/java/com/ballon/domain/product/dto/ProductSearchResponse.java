package com.ballon.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

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

    @Override
    public String toString() {
        return "ProductSearchResponse{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", partnerId=" + partnerId +
                ", partnerName='" + partnerName + '\'' +
                ", avgRating=" + avgRating +
                ", reviewCount=" + reviewCount +
                ", productId=" + productId +
                '}';
    }
}