package com.ballon.domain.wishlist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WishlistResponse {
    private Long productId;
    private String productUrl;
    private String name;
    private Integer price;
    private double avgRating;
    private Long reviewCount;

    @Override
    public String toString() {
        return "WishlistResponse{" +
                "reviewCount=" + reviewCount +
                ", avgRating=" + avgRating +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", productId=" + productId +
                '}';
    }
}
