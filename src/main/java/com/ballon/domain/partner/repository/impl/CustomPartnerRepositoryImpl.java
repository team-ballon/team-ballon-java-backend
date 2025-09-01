package com.ballon.domain.partner.repository.impl;

import com.ballon.domain.category.dto.CategoryResponse;
import com.ballon.domain.partner.dto.PartnerResponse;
import com.ballon.domain.partner.dto.PartnerSearchRequest;
import com.ballon.domain.partner.entity.Partner;
import com.ballon.domain.partner.entity.QPartner;
import com.ballon.domain.partner.entity.QPartnerCategory;
import com.ballon.domain.partner.repository.CustomPartnerRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomPartnerRepositoryImpl implements CustomPartnerRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PartnerResponse> search(PartnerSearchRequest req, Pageable pageable) {
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

        if (req.getCategoryIds() != null && !req.getCategoryIds().isEmpty()) {
            builder.and(partnerCategory.category.categoryId.in(req.getCategoryIds()));
        }

        // Content
        List<Partner> partners = queryFactory
                .selectFrom(partner)
                .leftJoin(partner.partnerCategory, partnerCategory).fetchJoin()
                .leftJoin(partnerCategory.category).fetchJoin() // 카테고리까지 조인
                .where(builder)
                .distinct()
                .orderBy(getOrderSpecifier(req.getSort(), partner))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<PartnerResponse> content = partners.stream()
                .map(p -> new PartnerResponse(
                        p.getUser().getUserId(),
                        p.getPartnerId(),
                        p.getPartnerEmail(),
                        p.getUser().getName(),
                        p.getOverview(),
                        p.getPartnerName(),
                        p.getPartnerCategory().stream()
                                .map(pc -> new CategoryResponse(
                                        pc.getCategory().getCategoryId(),
                                        pc.getCategory().getName()
                                )).toList()
                )).toList();

        // Count
        Long total = queryFactory
                .select(Wildcard.count)
                .from(partner)
                .leftJoin(partner.partnerCategory, partnerCategory)
                .where(builder)
                .distinct()
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort, QPartner partner) {
        if (sort == null) {
            return partner.partnerId.desc();
        }

        return switch (sort.toLowerCase()) {
            case "oldest" -> partner.partnerId.asc();
            case "name" -> partner.partnerName.asc();
            case "email" -> partner.partnerEmail.asc();
            default -> partner.partnerId.desc();
        };
    }
}
