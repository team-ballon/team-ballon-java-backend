package com.ballon.domain.product.repository;

import com.ballon.domain.product.dto.ProductBestRequest;
import com.ballon.domain.product.dto.ProductSearchRequest;
import com.ballon.domain.product.dto.ProductSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomProductRepository {
    Page<ProductSearchResponse> search(ProductSearchRequest req, List<Long> categoryIds, Pageable pageable);

    Page<ProductSearchResponse> findMonthlyBestSellers(ProductBestRequest req, List<Long> categoryIds, Pageable pageable);
}
