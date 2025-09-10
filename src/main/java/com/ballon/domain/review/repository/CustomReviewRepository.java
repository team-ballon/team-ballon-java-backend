package com.ballon.domain.review.repository;

import com.ballon.domain.review.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomReviewRepository {
    Page<ReviewResponse> searchReviews(Long productId, String sort, Pageable pageable);
}
