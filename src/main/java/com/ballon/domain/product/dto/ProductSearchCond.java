package com.ballon.domain.product.dto;

import com.ballon.domain.product.entity.Product;
import lombok.Data;

@Data
public class ProductSearchCond {

    // 상품명 부분 검색 (대소문자 구분없이 contains 검색)
    private String keyword;

    // 카테고리 필터: product.categry.id
    private  Long categoryId;

    // 파트너(입접업체) 필터: product.partne.id
    private  Long partnerId;

    //가격 필터: 최소값 (>=)
    private  Integer minPrice;

    // 가격 필터 최대값 (<=)
    private  Integer maxPrice;

    // 상태 필터: ACTIVE / INACTIVE / OUT_OF_STOCK
    private Product.Status status;

    // 재고 있는 것만 보기: quantity > 0
    private Boolean instockOnly;
}
