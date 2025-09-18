package com.ballon.domain.product.repository.impl;

import com.ballon.domain.category.entity.QCategory;
import com.ballon.domain.partner.entity.QPartner;
import com.ballon.domain.product.dto.ProductApplicationSearchRequest;
import com.ballon.domain.product.dto.ProductApplicationSearchResponse;
import com.ballon.domain.product.entity.QProduct;
import com.ballon.domain.product.entity.QProductApplication;
import com.ballon.domain.product.repository.CustomProductApplicationRepository;
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

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomProductApplicationRepositoryImpl implements CustomProductApplicationRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductApplicationSearchResponse> search(ProductApplicationSearchRequest req, Pageable pageable) {
        QProductApplication application = QProductApplication.productApplication;
        QPartner partner = QPartner.partner;
        QCategory category = QCategory.category;
        QProduct product = QProduct.product;

        BooleanBuilder builder = new BooleanBuilder();

        // --- 필터 조건 ---
        if (req.getName() != null && !req.getName().isBlank()) {
            builder.and(application.name.containsIgnoreCase(req.getName()));
        }
        if (req.getStatus() != null) {
            builder.and(application.status.eq(req.getStatus()));
        }
        if (req.getType() != null) {
            builder.and(application.type.eq(req.getType()));
        }
        if (req.getPartnerId() != null) {
            builder.and(application.partner.partnerId.eq(req.getPartnerId()));
        }

        // --- Count Query ---
        JPAQuery<Long> countQuery = queryFactory
                .select(application.count())
                .from(application)
                .where(builder);

        // --- Content Query ---
        List<ProductApplicationSearchResponse> content = queryFactory
                .select(Projections.constructor(
                        ProductApplicationSearchResponse.class,
                        application.productApplicationId,
                        application.name,
                        application.status,
                        application.type,
                        application.price,
                        application.quantity,
                        application.minQuantity,
                        application.applicationDate,
                        partner.partnerId,
                        partner.partnerName,
                        category.categoryId,
                        category.name,
                        product.id
                ))
                .from(application)
                .leftJoin(application.partner, partner)
                .leftJoin(application.category, category)
                .leftJoin(application.product, product)
                .where(builder)
                .orderBy(getOrderSpecifier(req.getSort(), application))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort, QProductApplication application) {
        if (sort == null) {
            return application.applicationDate.desc(); // 기본 정렬: 최신 신청순
        }

        return switch (sort.toLowerCase()) {
            case "oldest"  -> application.applicationDate.asc();
            case "name"    -> application.name.asc();
            case "price"   -> application.price.desc();
            case "quantity"-> application.quantity.desc();
            default        -> application.applicationDate.desc();
        };
    }
}
