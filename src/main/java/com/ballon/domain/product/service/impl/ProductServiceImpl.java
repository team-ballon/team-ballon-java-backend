package com.ballon.domain.product.service.impl;

import com.ballon.domain.product.dto.ProductSearchRequest;
import com.ballon.domain.product.dto.ProductSearchResponse;
import com.ballon.domain.product.repository.ProductRepository;
import com.ballon.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<ProductSearchResponse> searchProduct(ProductSearchRequest req, Pageable pageable) {
        return productRepository.search(req, pageable);
    }
}
