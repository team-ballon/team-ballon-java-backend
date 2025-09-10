package com.ballon.domain.review.repository.impl;

import com.ballon.domain.review.dto.ReviewResponse;
import com.ballon.domain.review.repository.CustomReviewRepository;
import com.ballon.domain.user.entity.QUser;
import com.ballon.domain.review.entity.QReview;
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
public class CustomReviewRepositoryImpl implements CustomReviewRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewResponse> searchReviews(Long productId, String sort, Pageable pageable) {
        QReview review = QReview.review;
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();
        if (productId != null) {
            builder.and(review.product.id.eq(productId));
        }

        // Count query
        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(builder);

        // Content query
        List<ReviewResponse> content = queryFactory
                .select(Projections.constructor(
                        ReviewResponse.class,
                        review.detail,
                        review.rating,
                        review.createdAt,
                        review.product.id,
                        user.name
                ))
                .from(review)
                .join(review.user, user)
                .where(builder)
                .orderBy(getOrderSpecifier(sort, review))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = countQuery.fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort, QReview review) {
        if (sort == null) {
            return review.createdAt.desc(); // 기본 최신순
        }

        return switch (sort.toLowerCase()) {
            case "ratingasc" -> review.rating.asc();
            case "ratingdesc" -> review.rating.desc();
            case "oldest" -> review.createdAt.asc();
            case "latest" -> review.createdAt.desc();
            default -> review.createdAt.desc();
        };
    }
}
