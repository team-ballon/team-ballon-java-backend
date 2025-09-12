package com.ballon.domain.review.service;

import com.ballon.domain.review.dto.ReviewRequest;
import com.ballon.domain.review.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    Page<ReviewResponse> getReviews(Long productId, String sort, Pageable pageable);

    ReviewResponse createReview(Long productId, ReviewRequest reviewRequest);

    ReviewResponse updateReview(Long productId, ReviewRequest reviewRequest);

    void deleteReview(Long productId);
}
