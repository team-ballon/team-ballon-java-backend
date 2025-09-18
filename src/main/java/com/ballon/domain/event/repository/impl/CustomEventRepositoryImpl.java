package com.ballon.domain.event.repository.impl;

import com.ballon.domain.event.dto.EventSearchRequest;
import com.ballon.domain.event.dto.EventSearchResponse;
import com.ballon.domain.event.repository.CustomEventRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.ballon.domain.event.entity.QEvent.event;

@RequiredArgsConstructor
public class CustomEventRepositoryImpl implements CustomEventRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<EventSearchResponse> searchEvents(EventSearchRequest request, Pageable pageable) {
        List<EventSearchResponse> results = queryFactory
                .select(Projections.constructor(
                        EventSearchResponse.class,
                        event.eventId,
                        event.title,
                        event.createdAt
                ))
                .from(event)
                .where(
                        keywordContains(request.getKeyword()),
                        startAfter(request.getStartDate()),
                        endBefore(request.getEndDate()),
                        ongoingOnly(request.getOnlyOngoing())
                )
                .orderBy(event.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(event.count())
                .from(event)
                .where(
                        keywordContains(request.getKeyword()),
                        startAfter(request.getStartDate()),
                        endBefore(request.getEndDate()),
                        ongoingOnly(request.getOnlyOngoing())
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(results, pageable, () -> total == null ? 0L : total);
    }

    private BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        return event.title.containsIgnoreCase(keyword)
                .or(event.description.containsIgnoreCase(keyword));
    }

    private BooleanExpression startAfter(LocalDateTime startDate) {
        return startDate != null ? event.startDate.goe(startDate) : null;
    }

    private BooleanExpression endBefore(LocalDateTime endDate) {
        return endDate != null ? event.endDate.loe(endDate) : null;
    }

    private BooleanExpression ongoingOnly(Boolean onlyOngoing) {
        if (onlyOngoing == null || !onlyOngoing) return null;
        LocalDateTime now = LocalDateTime.now();

        return event.startDate.loe(now).and(event.endDate.goe(now));
    }
}
