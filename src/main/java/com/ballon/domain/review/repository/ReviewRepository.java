package com.ballon.domain.review.repository;

import com.ballon.domain.review.entity.Review;
import com.ballon.domain.review.entity.type.ReviewId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, ReviewId>, CustomReviewRepository {
}
