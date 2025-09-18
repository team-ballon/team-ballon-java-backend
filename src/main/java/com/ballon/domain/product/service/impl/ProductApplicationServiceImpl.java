package com.ballon.domain.product.service.impl;

import com.ballon.domain.product.dto.ProductApplicationSearchRequest;
import com.ballon.domain.product.dto.ProductApplicationSearchResponse;
import com.ballon.domain.product.repository.ProductApplicationRepository;
import com.ballon.domain.product.service.ProductApplicationService;
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
public class ProductApplicationServiceImpl implements ProductApplicationService {

    private final ProductApplicationRepository productApplicationRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<ProductApplicationSearchResponse> searchApplications(ProductApplicationSearchRequest req, Pageable pageable) {
        log.debug("상품 신청 검색 요청: partnerId={}, status={}, page={}", req.getPartnerId(), req.getStatus(), pageable);

        Page<ProductApplicationSearchResponse> result = productApplicationRepository.search(req, pageable);

        log.info("상품 신청 검색 완료: 조회 건수={}, pageNumber={}", result.getTotalElements(), pageable.getPageNumber());
        return result;
    }
}