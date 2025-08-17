package com.ballon.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductSummaryDto {


    // 기본 상품 정보
    private Long id;
    private String name;
    private Integer price;
    private String status; // enum을 문자열로 내려주면 프론츠 처리/표시가 편하다고함
    private Integer quantity;

    // 표시용 연관 정보 (조인해서 한 번에 가져옴)
    private Long categoryId;
    private String categoryName;

    private Long parentId;
    private String parentName;
}
