package com.ballon.domain.product.controller;

import com.ballon.domain.product.dto.ProductSearchCond;
import com.ballon.domain.product.dto.ProductSummaryDto;
import com.ballon.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 예시) /api/products?keyword=선크림&categoryId=3&minPrice=10000&maxPrice=30000&status=ACTIVE&inStockOnly=true&sort=price,asc&page=0&size=20
    @GetMapping
    public Page<ProductSummaryDto> search(ProductSearchCond cond,
                                          @PageableDefault(size = 20) Pageable pageable) {
        return productService.search(cond, pageable);
    }

}
