package com.ballon.domain.product.service.impl;

import com.ballon.domain.product.dto.ProductResponse;
import com.ballon.domain.product.dto.ProductSearchRequest;
import com.ballon.domain.product.dto.ProductSearchResponse;
import com.ballon.domain.product.entity.Product;
import com.ballon.domain.product.repository.ProductRepository;
import com.ballon.domain.product.service.ProductService;
import com.ballon.global.common.exception.NotFoundException;
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

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));

        return new ProductResponse(
                product.getId(),
                product.getProductUrl(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getCreatedAt(),
                product.getCategory().getCategoryId(),
                product.getCategory().getName(),
                product.getPartner().getPartnerId(),
                product.getPartner().getPartnerName()
        );
    }
}
