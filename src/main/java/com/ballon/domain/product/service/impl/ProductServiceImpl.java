package com.ballon.domain.product.service.impl;

import com.ballon.domain.product.dto.ProductSearchCond;
import com.ballon.domain.product.dto.ProductSummaryDto;
import com.ballon.domain.product.repository.ProductRepository;
import com.ballon.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<ProductSummaryDto> search(ProductSearchCond cond, Pageable pageable) {
        return productRepository.search(cond, pageable);
    }
}
