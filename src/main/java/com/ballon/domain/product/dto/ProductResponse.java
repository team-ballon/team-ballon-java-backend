package com.ballon.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

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
}
