package com.ballon.domain.settlement.repository;

import com.ballon.domain.order.entity.QOrder;
import com.ballon.domain.order.entity.QOrderProduct;
import com.ballon.domain.order.entity.type.OrderStatus;
import com.ballon.domain.partner.entity.QPartner;
import com.ballon.domain.product.entity.QProduct;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class OrderSettlementRepository {

    private final JPAQueryFactory queryFactory;

    public Integer sumPartnerSales(Long partnerId, LocalDate start, LocalDate end) {
        QOrder order = QOrder.order;
        QOrderProduct orderProduct = QOrderProduct.orderProduct;
        QProduct product = QProduct.product;
        QPartner partner = QPartner.partner;

        Integer total = queryFactory
                .select(orderProduct.paidAmount.sum().coalesce(0))
                .from(order)
                .join(orderProduct).on(order.orderId.eq(orderProduct.order.orderId))
                .join(product).on(orderProduct.product.id.eq(product.id))
                .join(partner).on(product.partner.partnerId.eq(partner.partnerId))
                .where(order.status.eq(OrderStatus.DONE)
                        .and(order.createdAt.between(start.atStartOfDay(), end.atTime(23,59,59)))
                        .and(partner.partnerId.eq(partnerId)))
                .fetchOne();

        return total != null ? total : 0;
    }
}

