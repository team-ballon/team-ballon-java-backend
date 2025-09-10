package com.ballon.domain.review.entity;

import com.ballon.domain.product.entity.Product;
import com.ballon.domain.review.entity.type.ReviewId;
import com.ballon.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Review {

    @EmbeddedId
    private ReviewId reviewId;

    @Column(nullable = false, length = 1000)
    private String detail;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public static Review createReview(String detail, int rating, Product product, User user) {
        return Review.builder()
                .reviewId(new ReviewId(product.getId(), user.getUserId()))
                .detail(detail)
                .rating(rating)
                .product(product)
                .user(user)
                .build();
    }

    public void updateReview(String detail, int rating) {
        this.detail = detail;
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review that)) return false;
        return Objects.equals(reviewId, that.reviewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId);
    }
}
