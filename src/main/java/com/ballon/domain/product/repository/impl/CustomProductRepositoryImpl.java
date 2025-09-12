package com.ballon.domain.product.repository.impl;

import com.ballon.domain.order.entity.QOrder;
import com.ballon.domain.order.entity.QOrderProduct;
import com.ballon.domain.order.entity.type.OrderStatus;
import com.ballon.domain.product.dto.ProductBestRequest;
import com.ballon.domain.product.dto.ProductSearchRequest;
import com.ballon.domain.product.dto.ProductSearchResponse;
import com.ballon.domain.product.entity.QProduct;
import com.ballon.domain.product.repository.CustomProductRepository;
import com.ballon.domain.review.entity.QReview;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductSearchResponse> search(ProductSearchRequest req, List<Long> categoryIds, Pageable pageable) {
        QProduct product = QProduct.product;
        QReview review = QReview.review;
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
        if (categoryIds != null && !categoryIds.isEmpty()) {
            builder.and(product.category.categoryId.in(categoryIds));
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
                        product.partner.partnerName,
                        review.rating.avg().coalesce(0.0),
                        review.reviewId.countDistinct().coalesce(0L)
                ))
                .from(product)
                .leftJoin(review).on(review.product.id.eq(product.id))
                .where(builder)
                .groupBy(product.id,
                        product.productUrl,
                        product.name,
                        product.price,
                        product.partner.partnerId,
                        product.partner.partnerName)
                .orderBy(getOrderSpecifier(req.getSort(), product))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<ProductSearchResponse> findMonthlyBestSellers(ProductBestRequest req, List<Long> categoryIds, Pageable pageable) {
        LocalDateTime from = LocalDateTime.now().minusDays(30);

        QProduct product = QProduct.product;
        QOrderProduct orderProduct = QOrderProduct.orderProduct;
        QOrder order = QOrder.order;
        QReview review = QReview.review;

        BooleanBuilder builder = new BooleanBuilder()
                .and(order.status.eq(OrderStatus.DONE))
                .and(order.createdAt.goe(from));

        if (categoryIds != null && !categoryIds.isEmpty()) {
            builder.and(product.category.categoryId.in(categoryIds));
        }
        if (req.getPartnerId() != null) {
            builder.and(product.partner.partnerId.eq(req.getPartnerId()));
        }

        // --- Content Query ---
        List<ProductSearchResponse> results = queryFactory
                .select(Projections.constructor(
                        ProductSearchResponse.class,
                        product.id,
                        product.productUrl,
                        product.name,
                        product.price,
                        product.partner.partnerId,
                        product.partner.partnerName,
                        review.rating.avg().coalesce(0.0),
                        review.reviewId.countDistinct().coalesce(0L)
                ))
                .from(orderProduct)
                .join(order).on(orderProduct.order.orderId.eq(order.orderId))
                .join(product).on(orderProduct.product.id.eq(product.id))
                .leftJoin(review).on(review.product.id.eq(product.id))
                .where(builder)
                .groupBy(product.id,
                        product.productUrl,
                        product.name,
                        product.price,
                        product.partner.partnerId,
                        product.partner.partnerName)
                .orderBy(orderProduct.quantity.sum().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // --- Count Query ---
        Long total = queryFactory
                .select(product.id.countDistinct())
                .from(orderProduct)
                .join(order).on(orderProduct.order.orderId.eq(order.orderId))
                .join(product).on(orderProduct.product.id.eq(product.id))
                .where(builder)
                .fetchOne();

        return PageableExecutionUtils.getPage(results, pageable, () -> total == null ? 0L : total);
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
