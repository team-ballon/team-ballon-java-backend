package com.ballon.domain.product.dto;

import com.ballon.domain.product.entity.type.ProductApplicationStatus;
import com.ballon.domain.product.entity.type.ProductApplicationType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductApplicationSearchRequest {
    private String name;                     // 신청 상품명 검색
    private ProductApplicationStatus status; // 상태 필터 (PENDING, APPROVED, DENIED)
    private ProductApplicationType type;     // 신청 타입 (CREATE, UPDATE, REMOVE)
    private Long partnerId;                  // 파트너 ID
    private String sort;                     // 정렬 조건 (oldest, name, price, quantity 등)
}
