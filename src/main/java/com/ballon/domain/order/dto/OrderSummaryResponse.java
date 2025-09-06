package com.ballon.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class OrderSummaryResponse {
    private Long orderProductId;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private int paidAmount;
    private int quantity;
    private LocalDateTime createdAt;
}
