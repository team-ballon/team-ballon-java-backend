package com.ballon.domain.product.repository.ProductRepositoryCustom;

import com.ballon.domain.product.dto.ProductSearchCond;
import com.ballon.domain.product.dto.ProductSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<ProductSummaryDto> search(ProductSearchCond cond, Pageable pageable);
}
