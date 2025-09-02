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

        boolean hasCategoryFilter = req.getCategoryIds() != null && !req.getCategoryIds().isEmpty();
        if (hasCategoryFilter) {
            builder.and(partnerCategory.category.categoryId.in(req.getCategoryIds()));
        }

        var query = queryFactory
                .selectFrom(partner)
                .distinct()
                .orderBy(getOrderSpecifier(req.getSort(), partner))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (hasCategoryFilter) {
            query.innerJoin(partner.partnerCategory, partnerCategory).fetchJoin()
                    .innerJoin(partnerCategory.category).fetchJoin();
        } else {
            query.leftJoin(partner.partnerCategory, partnerCategory).fetchJoin()
                    .leftJoin(partnerCategory.category).fetchJoin();
        }

        List<Partner> partners = query.where(builder).fetch();

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
                                ))
                                .toList()
                ))
                .toList();

        Long total;
        var countQuery = queryFactory
                .select(partner.partnerId.countDistinct())
                .from(partner);

        if (hasCategoryFilter) {
            countQuery.innerJoin(partner.partnerCategory, partnerCategory)
                    .innerJoin(partnerCategory.category);
        }

        total = countQuery.where(builder).fetchOne();

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
