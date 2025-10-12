package com.ballon.domain.product.service.impl;

import com.ballon.domain.coupon.dto.CouponResponse;
import com.ballon.domain.coupon.repository.CouponRepository;
import com.ballon.domain.product.dto.ProductBestRequest;
import com.ballon.domain.product.dto.ProductResponse;
import com.ballon.domain.product.dto.ProductSearchRequest;
import com.ballon.domain.product.dto.ProductSearchResponse;
import com.ballon.domain.product.entity.Product;
import com.ballon.domain.coupon.repository.CouponProductRepository;
import com.ballon.domain.product.repository.ImageLinkRepository;
import com.ballon.domain.product.repository.ProductRepository;
import com.ballon.domain.product.service.ProductService;
import com.ballon.global.cache.CategoryCacheStore;
import com.ballon.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryCacheStore categoryCacheStore;
    private final CouponProductRepository couponProductRepository;
    private final CouponRepository couponRepository;
    private final ImageLinkRepository imageLinkRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<ProductSearchResponse> searchProduct(ProductSearchRequest req, Pageable pageable) {
        log.debug("상품 검색 요청: categoryId={}, keyword={}, page={}", req.getCategoryId(), req.getName(), pageable);

        CategoryCacheStore.Node category = categoryCacheStore.getById(req.getCategoryId());
        List<Long> categoryIds = new ArrayList<>();

        if (Objects.nonNull(category)) {
            if (!category.children.isEmpty()) {
                categoryIds.addAll(category.children);
                log.debug("하위 카테고리 포함 검색: categoryIds={}", categoryIds);
            } else {
                categoryIds.add(req.getCategoryId());
                log.debug("단일 카테고리 검색: categoryId={}", req.getCategoryId());
            }
        }

        Page<ProductSearchResponse> result = productRepository.search(req, categoryIds, pageable);
        log.info("상품 검색 완료: 조회 건수={}, pageNumber={}", result.getTotalElements(), pageable.getPageNumber());
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProduct(Long productId) {
        log.debug("상품 상세 조회 요청: productId={}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));

        List<String> imageLinks = imageLinkRepository.findLinksByProductId(productId);
        log.debug("상품 이미지 조회 완료: productId={}, 이미지 개수={}", productId, imageLinks.size());

        List<Long> couponIds = couponProductRepository.findCouponIdsByProductId(productId);
        log.debug("상품 쿠폰 ID 조회 완료: productId={}, couponIds={}", productId, couponIds);

        List<CouponResponse> couponResponses = couponIds.isEmpty()
                ? List.of()
                : couponRepository.findAllByIdWithEvent(couponIds).stream()
                .map(coupon -> new CouponResponse(
                        coupon.getCouponId(),
                        coupon.getCouponName(),
                        coupon.getDiscountValue(),
                        coupon.getType().toString(),
                        coupon.getEvent().getStartDate(),
                        coupon.getEvent().getEndDate()
                ))
                .toList();

        log.info("상품 상세 조회 완료: productId={}, 쿠폰 개수={}, 이미지 개수={}", productId, couponResponses.size(), imageLinks.size());

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
                product.getPartner().getPartnerName(),
                couponResponses,
                imageLinks
        );
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductSearchResponse> findMonthlyBestSellers(ProductBestRequest req, Pageable pageable) {
        log.debug("월간 베스트셀러 조회 요청: categoryId={}, page={}", req.getCategoryId(), pageable);

        CategoryCacheStore.Node category = categoryCacheStore.getById(req.getCategoryId());
        List<Long> categoryIds = new ArrayList<>();

        if (Objects.nonNull(category)) {
            if (!category.children.isEmpty()) {
                categoryIds.addAll(category.children);
                log.debug("하위 카테고리 포함 베스트셀러 조회: categoryIds={}", categoryIds);
            } else {
                categoryIds.add(req.getCategoryId());
                log.debug("단일 카테고리 베스트셀러 조회: categoryId={}", req.getCategoryId());
            }
        }

        Page<ProductSearchResponse> result = productRepository.findMonthlyBestSellers(req, categoryIds, pageable);
        log.info("월간 베스트셀러 조회 완료: 조회 건수={}, pageNumber={}", result.getTotalElements(), pageable.getPageNumber());
        return result;
    }
}
