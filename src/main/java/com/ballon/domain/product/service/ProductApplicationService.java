package com.ballon.domain.product.service;

import com.ballon.domain.product.dto.ProductApplicationSearchRequest;
import com.ballon.domain.product.dto.ProductApplicationSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductApplicationService {
    Page<ProductApplicationSearchResponse> searchApplications(ProductApplicationSearchRequest req, Pageable pageable);
}
