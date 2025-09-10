package com.ballon.domain.product.controller;

import com.ballon.domain.keyword.service.KeywordService;
import com.ballon.domain.product.dto.ProductBestRequest;
import com.ballon.domain.product.dto.ProductResponse;
import com.ballon.domain.product.dto.ProductSearchRequest;
import com.ballon.domain.product.dto.ProductSearchResponse;
import com.ballon.domain.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "상품 관리 API", description = "상품과 관련된 기능")
public class ProductController {
    private final ProductService productService;
    private final KeywordService keywordService;

    @Operation(
            summary = "상품 검색",
            description = "이름, 가격 범위, 생성일, 카테고리, 파트너 조건으로 상품을 검색합니다."
    )
    @ApiResponse(responseCode = "200", description = "검색 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductSearchResponse.class))))
    @GetMapping("/search")
    public Page<ProductSearchResponse> searchProducts(
            @Parameter(description = "검색 조건") ProductSearchRequest productSearchRequest,
            Pageable pageable
    ) {
        String keyword = productSearchRequest.getName();
        if (keyword != null && !keyword.isBlank()) {
            keywordService.saveKeyword(keyword);
        }

        return productService.searchProduct(productSearchRequest, pageable);
    }

    @Operation(
            summary = "상품 조회",
            description = "상품 Id로 상품을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품")
    })
    @GetMapping("/{product-id}")
    public ProductResponse getProduct(@PathVariable("product-id") Long productId) {
        return productService.getProduct(productId);
    }

    @Operation(
            summary = "베스트 상품 검색",
            description = "카테고리 id, 파트너 id로 베스트 상품을 검색합니다."
    )
    @ApiResponse(responseCode = "200", description = "검색 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductSearchResponse.class))))
    @GetMapping("/best")
    public Page<ProductSearchResponse> searchBestProducts(ProductBestRequest productBestRequest, Pageable pageable) {
        return productService.findMonthlyBestSellers(productBestRequest, pageable);
    }
}
