package com.ballon.domain.product.service.impl;

import com.ballon.domain.coupon.dto.CouponResponse;
import com.ballon.domain.coupon.entity.Coupon;
import com.ballon.domain.coupon.repository.CouponRepository;
import com.ballon.domain.product.dto.ProductResponse;
import com.ballon.domain.product.dto.ProductSearchRequest;
import com.ballon.domain.product.dto.ProductSearchResponse;
import com.ballon.domain.product.entity.ImageLink;
import com.ballon.domain.product.entity.Product;
import com.ballon.domain.product.repository.CouponProductRepository;
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
        CategoryCacheStore.Node category = categoryCacheStore.getById(req.getCategoryId());
        List<Long> categoryIds = new ArrayList<>();

        if (!category.children.isEmpty()) {
            categoryIds.addAll(category.children);
        } else {
            categoryIds.add(req.getCategoryId());
        }

        return productRepository.search(req, categoryIds, pageable);
    }


    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));

        List<String> imageLinks = imageLinkRepository.findLinksByProductId(productId);

        List<Long> couponIds = couponProductRepository.findCouponIdsByProductId(productId);

        List<CouponResponse> couponResponses = couponIds.isEmpty()
                ? List.of()
                : couponRepository.findAllByIdWithEvent(couponIds).stream()
                .map(coupon -> new CouponResponse(
                        coupon.getCouponId(),
                        coupon.getCouponName(),
                        coupon.getDiscount(),
                        coupon.getType().toString(),
                        coupon.getEvent().getStartDate(),
                        coupon.getEvent().getEndDate()
                ))
                .toList();

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
}
