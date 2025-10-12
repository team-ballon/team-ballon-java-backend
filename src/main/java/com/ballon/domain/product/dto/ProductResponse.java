package com.ballon.domain.product.dto;

import com.ballon.domain.coupon.dto.CouponResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private String productUrl;
    private String productName;
    private Integer productPrice;
    private Integer productQuantity;
    private LocalDateTime createdAt;
    private Long categoryId;
    private String categoryName;
    private Long partnerId;
    private String partnerName;
    private List<CouponResponse> coupons;
    private List<String> detailImageLinks;

    @Override
    public String toString() {
        return "ProductResponse{" +
                "coupons=" + coupons +
                ", partnerName='" + partnerName + '\'' +
                ", partnerId=" + partnerId +
                ", categoryName='" + categoryName + '\'' +
                ", categoryId=" + categoryId +
                ", createdAt=" + createdAt +
                ", productQuantity=" + productQuantity +
                ", productPrice=" + productPrice +
                ", productName='" + productName + '\'' +
                ", productId=" + productId +
                ", detailImageLinks count=" + detailImageLinks.size() +
                '}';
    }
}
