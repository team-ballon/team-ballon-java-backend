package com.ballon.domain.product.repository;

import com.ballon.domain.product.dto.ProductApplicationSearchRequest;
import com.ballon.domain.product.dto.ProductApplicationSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomProductApplicationRepository {
    Page<ProductApplicationSearchResponse> search(ProductApplicationSearchRequest req, Pageable pageable);
}
