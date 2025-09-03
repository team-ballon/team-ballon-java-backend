package com.ballon.domain.product.repository.impl;

import com.ballon.domain.product.dto.ProductSearchRequest;
import com.ballon.domain.product.dto.ProductSearchResponse;
import com.ballon.domain.product.entity.QProduct;
import com.ballon.domain.product.repository.CustomProductRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductSearchResponse> search(ProductSearchRequest req, Pageable pageable) {
        QProduct product = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        // --- 필터 조건 ---
        if (req.getName() != null && !req.getName().isBlank()) {
            builder.and(product.name.containsIgnoreCase(req.getName()));
        }
        if (req.getMinPrice() != null) {
            builder.and(product.price.goe(req.getMinPrice()));
        }
        if (req.getMaxPrice() != null) {
            builder.and(product.price.loe(req.getMaxPrice()));
        }
        if (req.getStartDate() != null) {
            builder.and(product.createdAt.goe(req.getStartDate()));
        }
        if (req.getEndDate() != null) {
            builder.and(product.createdAt.loe(req.getEndDate()));
        }
        if (req.getCategoryId() != null) {
            builder.and(product.category.categoryId.eq(req.getCategoryId()));
        }
        if (req.getPartnerId() != null) {
            builder.and(product.partner.partnerId.eq(req.getPartnerId()));
        }

        // --- Count Query ---
        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(builder);

        // --- Content Query ---
        List<ProductSearchResponse> content = queryFactory
                .select(Projections.constructor(
                        ProductSearchResponse.class,
                        product.id,
                        product.productUrl,
                        product.name,
                        product.price,
                        product.partner.partnerId,
                        product.partner.partnerName
                ))
                .from(product)
                .where(builder)
                .orderBy(getOrderSpecifier(req.getSort(), product))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = countQuery.fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort, QProduct product) {
        if (sort == null) {
            return product.createdAt.desc(); // 기본: 최신순
        }

        return switch (sort.toLowerCase()) {
            case "oldest"     -> product.createdAt.asc();
            case "name"       -> product.name.asc();
            case "price_low"  -> product.price.asc();
            case "price_high" -> product.price.desc();
            default           -> product.createdAt.desc();
        };
    }
}
