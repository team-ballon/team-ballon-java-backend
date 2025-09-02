package com.ballon.domain.user.repository.impl;

import com.ballon.domain.user.dto.UserResponse;
import com.ballon.domain.user.dto.UserSearchRequest;
import com.ballon.domain.user.dto.UserSearchResponse;
import com.ballon.domain.user.entity.QUser;
import com.ballon.domain.user.repository.CustomUserRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<UserSearchResponse> search(UserSearchRequest req, Pageable pageable) {
        QUser user = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();

        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            builder.and(user.email.containsIgnoreCase(req.getEmail()));
        }
        if (req.getName() != null && !req.getName().isBlank()) {
            builder.and(user.name.containsIgnoreCase(req.getName()));
        }
        if (req.getMinAge() != null) {
            builder.and(user.age.goe(req.getMinAge()));
        }
        if (req.getMaxAge() != null) {
            builder.and(user.age.loe(req.getMaxAge()));
        }
        if (req.getSex() != null && !req.getSex().isBlank()) {
            builder.and(user.sex.stringValue().eq(req.getSex().toUpperCase()));
        }
        if (req.getRole() != null && !req.getRole().isBlank()) {
            builder.and(user.role.stringValue().eq(req.getRole().toUpperCase()));
        }

        // Count query
        JPAQuery<Long> countQuery = queryFactory
                .select(user.userId.count())
                .from(user)
                .where(builder);

        // Content query
        List<UserSearchResponse> content = queryFactory
                .select(Projections.constructor(
                        UserSearchResponse.class,
                        user.userId,
                        user.email,
                        user.role.stringValue(),
                        user.createdAt
                ))
                .from(user)
                .where(builder)
                .orderBy(getOrderSpecifier(req.getSort(), user))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = countQuery.fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort, QUser user) {
        if (sort == null) {
            return user.userId.desc();
        }

        return switch (sort.toLowerCase()) {
            case "oldest" -> user.userId.asc();
            case "name" -> user.name.asc();
            case "email" -> user.email.asc();
            default -> user.userId.desc();
        };
    }
}
