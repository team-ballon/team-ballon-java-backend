package com.ballon.domain.settlement.repository.impl;

import com.ballon.domain.partner.entity.QPartner;
import com.ballon.domain.settlement.dto.SettlementSearchRequest;
import com.ballon.domain.settlement.dto.SettlementSearchResponse;
import com.ballon.domain.settlement.entity.QSettlement;
import com.ballon.domain.settlement.repository.CustomSettlementRepository;
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
public class CustomSettlementRepositoryImpl implements CustomSettlementRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SettlementSearchResponse> search(SettlementSearchRequest req, Pageable pageable) {
        QSettlement settlement = QSettlement.settlement;
        QPartner partner = QPartner.partner;

        BooleanBuilder builder = new BooleanBuilder();

        if (req.getPartnerId() != null) {
            builder.and(settlement.partner.partnerId.eq(req.getPartnerId()));
        }
        if (req.getStatus() != null) {
            builder.and(settlement.status.eq(req.getStatus()));
        }
        if (req.getStartDate() != null) {
            builder.and(settlement.periodStart.goe(req.getStartDate()));
        }
        if (req.getEndDate() != null) {
            builder.and(settlement.periodEnd.loe(req.getEndDate()));
        }

        // Count
        JPAQuery<Long> countQuery = queryFactory
                .select(settlement.count())
                .from(settlement)
                .where(builder);

        // Content
        List<SettlementSearchResponse> content = queryFactory
                .select(Projections.constructor(
                        SettlementSearchResponse.class,
                        settlement.settlementId,
                        partner.partnerId,
                        partner.partnerName,
                        settlement.periodStart,
                        settlement.periodEnd,
                        settlement.totalAmount,
                        settlement.status,
                        settlement.createdAt
                ))
                .from(settlement)
                .join(settlement.partner, partner)
                .where(builder)
                .orderBy(getOrderSpecifier(req.getSort(), settlement))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort, QSettlement settlement) {
        if (sort == null) {
            return settlement.createdAt.desc();
        }

        return switch (sort.toLowerCase()) {
            case "oldest"     -> settlement.createdAt.asc();
            case "amount_high"-> settlement.totalAmount.desc();
            case "amount_low" -> settlement.totalAmount.asc();
            default           -> settlement.createdAt.desc();
        };
    }
}
