package com.ballon.domain.product.controller;

import com.ballon.domain.product.dto.ProductSearchCond;
import com.ballon.domain.product.dto.ProductSummaryDto;
import com.ballon.domain.product.entity.Product;
import com.ballon.domain.product.service.ProductService;
import com.ballon.domain.keyword.service.KeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final KeywordService keywordService;

    // 예시 /api/products?keyword=선크림&categoryId=3&minPrice=10000&maxPrice=30000&status=ACTIVE&inStockOnly=true&sort=price,asc&page=0&size=20
    @Operation(summary = "상품 검색/필터",
            description = "키워드, 카테고리, 파트어, 가격범위, 재고유무, 상태, 정렬/페이지네이션으로 검색합니다.")
    @GetMapping
    public Page<ProductSummaryDto> search(
            @Parameter(description = "상품명 키워드(부분일치, 대소문자 무시)")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "카테고리 ID")
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "파트너 ID")
            @RequestParam(required = false) Long parentId,
            @Parameter(description = "최소 가격")
            @RequestParam(required = false) Integer minPrice,
            @Parameter(description = "최대 가격")
            @RequestParam(required = false) Integer maxPrice,
            @Parameter(description = "재고 상품만(수량>0")
            @RequestParam(required = false, defaultValue = "false") boolean inStockOnly,
            @Parameter(description = "상품 상태 (ACTIVE/INACTIVE/OUT_OF_STOCK)")
            @RequestParam(required = false) Product.Status status,
            @ParameterObject Pageable pageable
    ){
        ProductSearchCond cond = new ProductSearchCond();
        cond.setKeyword(keyword);
        cond.setCategoryId(categoryId);
        cond.setPartnerId(parentId);
        cond.setMinPrice(minPrice);
        cond.setMaxPrice(maxPrice);
        cond.setInstockOnly(inStockOnly);
        cond.setStatus(status);

        // 인기 검색어 기록 (keyword 있으면)
        if (keyword != null && !keyword.isBlank()) {
            keywordService.record(keyword);
        }

        return productService.search(cond, pageable);
    }

}
