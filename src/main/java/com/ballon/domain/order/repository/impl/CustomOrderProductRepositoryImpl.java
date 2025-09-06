package com.ballon.domain.order.repository.impl;

import com.ballon.domain.order.dto.OrderSummaryResponse;
import com.ballon.domain.order.entity.OrderProduct;
import com.ballon.domain.order.entity.QOrder;
import com.ballon.domain.order.entity.QOrderProduct;
import com.ballon.domain.order.repository.CustomOrderProductRepository;
import com.ballon.domain.product.entity.QProduct;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class CustomOrderProductRepositoryImpl implements CustomOrderProductRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderSummaryResponse> findAllByUserId(Long userId, Pageable pageable) {
        QOrderProduct orderProduct = QOrderProduct.orderProduct;
        QOrder order = QOrder.order;
        QProduct product = QProduct.product;

        List<OrderSummaryResponse> content = queryFactory
                .select(Projections.constructor(OrderSummaryResponse.class,
                        orderProduct.orderProductId,
                        product.id,
                        product.name,
                        product.productUrl,
                        orderProduct.paidAmount,
                        orderProduct.quantity,
                        orderProduct.createdAt
                ))
                .from(orderProduct)
                .join(orderProduct.order, order)
                .join(orderProduct.product, product)
                .where(order.user.userId.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderProduct.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(orderProduct.count())
                .from(orderProduct)
                .join(orderProduct.order, order)
                .where(order.user.userId.eq(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

}
