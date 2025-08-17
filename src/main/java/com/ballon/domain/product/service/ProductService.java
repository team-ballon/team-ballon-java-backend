package com.ballon.domain.product.service;


import com.ballon.domain.product.dto.ProductSearchCond;
import com.ballon.domain.product.dto.ProductSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductSummaryDto> search(ProductSearchCond cond, Pageable pageable);
}
