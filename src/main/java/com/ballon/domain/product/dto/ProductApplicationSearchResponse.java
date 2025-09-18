package com.ballon.domain.product.dto;

import com.ballon.domain.product.entity.type.ProductApplicationStatus;
import com.ballon.domain.product.entity.type.ProductApplicationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductApplicationSearchResponse {
    private Long applicationId;              // 신청 ID
    private String name;                     // 신청 상품명
    private ProductApplicationStatus status; // 상태
    private ProductApplicationType type;     // 신청 타입
    private Integer price;                   // 신청 가격
    private Integer quantity;                // 신청 수량
    private Integer minQuantity;             // 최소 주문 수량
    private LocalDateTime applicationDate;   // 신청 일자
    private Long partnerId;                  // 파트너 ID
    private String partnerName;              // 파트너 이름
    private Long categoryId;                 // 카테고리 ID
    private String categoryName;             // 카테고리 이름
    private Long productId;                  // 기존 상품 ID (수정/삭제 신청인 경우)
}
