package com.ballon.domain.product.controller;

import com.ballon.domain.product.dto.ProductSearchRequest;
import com.ballon.domain.product.dto.ProductSearchResponse;
import com.ballon.domain.product.repository.ProductRepository;
import com.ballon.domain.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "상품 관리 API", description = "상품과 관련된 기능")
public class ProductController {
    private final ProductService productService;

    @Operation(
            summary = "상품 검색",
            description = "이름, 가격 범위, 생성일, 카테고리, 파트너 조건으로 상품을 검색합니다."
    )
    @ApiResponse(responseCode = "200", description = "검색 성공",
            content = @Content(schema = @Schema(implementation = ProductSearchResponse.class)))
    @GetMapping("/search")
    public Page<ProductSearchResponse> searchProducts(
            @Parameter(description = "검색 조건") ProductSearchRequest productSearchRequest,
            Pageable pageable
    ) {
        return productService.searchProduct(productSearchRequest, pageable);
    }
}
