package com.ballon.domain.product.service;

import com.ballon.domain.product.dto.ProductSearchRequest;
import com.ballon.domain.product.dto.ProductSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductSearchResponse> searchProduct(ProductSearchRequest req, Pageable pageable);
}
