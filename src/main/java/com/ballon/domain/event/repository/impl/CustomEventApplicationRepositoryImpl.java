package com.ballon.domain.event.repository.impl;

import com.ballon.domain.event.dto.EventSearchApplicationRequest;
import com.ballon.domain.event.dto.EventApplicationResponse;
import com.ballon.domain.event.entity.type.EventStatus;
import com.ballon.domain.event.repository.CustomEventApplicationRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.ballon.domain.event.entity.QEvent.event;
import static com.ballon.domain.event.entity.QEventApplication.eventApplication;
import static com.ballon.domain.partner.entity.QPartner.partner;

@RequiredArgsConstructor
public class CustomEventApplicationRepositoryImpl implements CustomEventApplicationRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<EventApplicationResponse> searchApplications(EventSearchApplicationRequest request, Pageable pageable) {
        List<EventApplicationResponse> results = queryFactory
                .select(Projections.constructor(
                        EventApplicationResponse.class,
                        eventApplication.eventApplicationId,
                        eventApplication.status,
                        eventApplication.applicationDate,
                        event.title,
                        partner.partnerName,
                        partner.partnerEmail
                ))
                .from(eventApplication)
                .join(eventApplication.event, event)
                .join(eventApplication.partner, partner)
                .where(
                        statusEq(request.getStatus()),
                        eventIdEq(request.getEventId()),
                        partnerNameContains(request.getPartnerName())
                )
                .orderBy(eventApplication.applicationDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(eventApplication.count())
                .from(eventApplication)
                .join(eventApplication.event, event)
                .join(eventApplication.partner, partner)
                .where(
                        statusEq(request.getStatus()),
                        eventIdEq(request.getEventId()),
                        partnerNameContains(request.getPartnerName())
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(results, pageable, () -> total == null ? 0L : total);
    }

    private BooleanExpression statusEq(EventStatus status) {
        return status != null ? eventApplication.status.eq(status) : null;
    }

    private BooleanExpression eventIdEq(Long eventId) {
        return eventId != null ? event.eventId.eq(eventId) : null;
    }

    private BooleanExpression partnerNameContains(String partnerName) {
        return (partnerName != null && !partnerName.isBlank()) ? partner.partnerName.containsIgnoreCase(partnerName) : null;
    }
}
