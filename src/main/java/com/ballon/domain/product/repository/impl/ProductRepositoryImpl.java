package com.ballon.domain.product.repository.impl;

import com.ballon.domain.product.dto.ProductSearchCond;
import com.ballon.domain.product.dto.ProductSummaryDto;
import com.ballon.domain.product.repository.ProductRepositoryCustom.ProductRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QueryFactory queryFactory;

    @Override
    public Page<ProductSummaryDto> search(ProductSearchCond cond, Pageable pageable) {
    QProdcut p = QProduct.product;
    QCategry c = Qcategory.cateory;
    QPatner ptn = QPartner.partner;

    // 동적 where
        BooleanBuilder where = new BooleanBuilder();
        if (cond.getKeyword() != null && !cond.getKeyword().isBlank()) {
            where.and(p.name.containsIgnoreCase(cond.getKeyword().trim()));
        }
        if (cond.getCategoryId() != null) {
            where.and(p.category.id.eq(cond.getCategoryId()));
        }
        if (cond.getPartnerId() != null) {
            where.and(p.partner.id.eq(cond.getPartnerId()));
        }
        if (cond.getMinPrice() != null) {
            where.and(p.price.goe(cond.getMinPrice()));
        }
        if (cond.getMaxPrice() != null) {
            where.and(p.price.loe(cond.getMaxPrice()));
        }
        if (cond.getStatus() != null) {
            where.and(p.status.eq(cond.getStatus()));
        }
        if (Boolean.TRUE.equals(cond.getInstockOnly())){
            where.and(p.quantity.gt(0));
        }

        // 목록 (DTO 생성자 프로젝션)
        List<ProductSummaryDto> content = queryFactory
                .select(Projections.constructor(ProductSummaryDto.class,
                        p.id, p.name, p.price, p.status.stringValue(), p.quantity,
                        p.category.id, p.category.name,
                        p.partner.id, p.partner.name
                ))
                .from(p)
                .join(p.category, c)
                .join(p.partner, ptn)
                .where(where)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(toOrderSpecifiers(pageable.getSort(), p))
                .fetch();
        // 총 개수
        Long total = queryFactory
                .select(p.count())
                .from(p)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total =null ? 0 : total);
    }

    // Pageable 정렬 -> 안전한 OrderSpecifier 로 변환
    private OrderSpecifier<?>[] toOrderSpecifiers(Sort sort, QProduct p) {
        if (sort == null || sort.isUnsorted()) {
            return new OrderSpecifier[]{ p.id.desc() }; //  기본 정렬
        }
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (Sort.Order o : sort) {
            Order dir = o.isAscending() ? Order.ASC : Order.DESC;
            switch (o.getProperty()) {
                case "id": orders.add(new OrderSpecifier<>(dir, p.id)); break;
                case "price": orders.add(new OrderSpecifier<>(dir, p.price)); break;
                case "name": orders.add(new OrderSpecifier<>(dir, p.name)); break;
                case "quantity": orders.add(new OrderSpecifier<>(dir, p.quantity)); break;
                default: // 허용하지 않은 정렬 키는 무시
            }
        }
        if (orders.isEmpty()) orders.add(p.id.desc());
        return orders.toArray(new OrderSpecifier<?>[0]);
    }
}
