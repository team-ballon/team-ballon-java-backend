package com.ballon.domain.partner.repository.impl;

import com.ballon.domain.partner.dto.PartnerSearchRequest;
import com.ballon.domain.partner.dto.PartnerSearchResponse;
import com.ballon.domain.partner.entity.QPartner;
import com.ballon.domain.partner.entity.QPartnerCategory;
import com.ballon.domain.partner.repository.CustomPartnerRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomPartnerRepositoryImpl implements CustomPartnerRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PartnerSearchResponse> search(PartnerSearchRequest req, Pageable pageable) {
        QPartner partner = QPartner.partner;
        QPartnerCategory partnerCategory = QPartnerCategory.partnerCategory;

        BooleanBuilder builder = new BooleanBuilder();

        if (req.getName() != null && !req.getName().isBlank()) {
            builder.and(partner.partnerName.containsIgnoreCase(req.getName()));
        }
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            builder.and(partner.partnerEmail.containsIgnoreCase(req.getEmail()));
        }
        if (req.getActive() != null) {
            builder.and(partner.active.eq(req.getActive()));
        }

        boolean hasCategoryFilter = req.getCategoryIds() != null && !req.getCategoryIds().isEmpty();

        // --- Count Query ---
        JPAQuery<Long> countQuery = queryFactory
                .select(partner.partnerId.countDistinct())
                .from(partner);

        if (hasCategoryFilter) {
            countQuery.innerJoin(partner.partnerCategory, partnerCategory)
                    .where(partnerCategory.category.categoryId.in(req.getCategoryIds()));
        }

        // --- Content Query ---
        List<PartnerSearchResponse> content = queryFactory
                .select(Projections.constructor(
                        PartnerSearchResponse.class,
                        partner.partnerId,
                        partner.partnerName,
                        partner.active,
                        partner.createdAt
                ))
                .from(partner)
                .distinct()
                .where(builder)
                .orderBy(getOrderSpecifier(req.getSort(), partner))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = countQuery.where(builder).fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort, QPartner partner) {
        if (sort == null) {
            return partner.createdAt.desc(); // 기본값 최신순
        }

        return switch (sort.toLowerCase()) {
            case "oldest" -> partner.createdAt.asc();  // 오래된 순
            case "name" -> partner.partnerName.asc();
            case "email" -> partner.partnerEmail.asc();
            default -> partner.createdAt.desc();
        };
    }
}
