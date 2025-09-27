package com.ballon.domain.review.service.impl;

import com.ballon.domain.order.repository.OrderProductRepository;
import com.ballon.domain.product.entity.Product;
import com.ballon.domain.product.repository.ProductRepository;
import com.ballon.domain.review.dto.ReviewRequest;
import com.ballon.domain.review.dto.ReviewResponse;
import com.ballon.domain.review.entity.Review;
import com.ballon.domain.review.entity.type.ReviewId;
import com.ballon.domain.review.repository.ReviewRepository;
import com.ballon.domain.review.service.ReviewService;
import com.ballon.domain.user.entity.User;
import com.ballon.domain.user.repository.UserRepository;
import com.ballon.global.UserUtil;
import com.ballon.global.common.exception.ForbiddenException;
import com.ballon.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final OrderProductRepository orderProductRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<ReviewResponse> getReviews(Long productId, String sort, Pageable pageable) {
        log.debug("리뷰 조회 요청: productId={}, sort={}, page={}", productId, sort, pageable);
        Page<ReviewResponse> result = reviewRepository.searchReviews(productId, sort, pageable);
        log.info("리뷰 조회 완료: productId={}, 총 건수={}", productId, result.getTotalElements());
        return result;
    }

    @Override
    public ReviewResponse createReview(Long productId, ReviewRequest reviewRequest) {
        Long userId = UserUtil.getUserId();
        log.debug("리뷰 작성 요청: userId={}, productId={}, rating={}", userId, productId, reviewRequest.getRating());

        User user = userRepository.getReferenceById(userId);

        if (!orderProductRepository.existsPurchasedProductByUser(userId, productId)) {
            throw new ForbiddenException("구매 이력이 없는 상품에는 리뷰를 작성할 수 없습니다.");
        }

        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("존재하지 않는 상품입니다.");
        }

        Product product = productRepository.getReferenceById(productId);

        Review review = Review.createReview(
                reviewRequest.getDetail(),
                reviewRequest.getRating(),
                product,
                user
        );

        reviewRepository.save(review);
        log.info("리뷰 작성 성공: userId={}, productId={}, rating={}, detail='{}'",
                userId, productId, review.getRating(), review.getDetail());

        return new ReviewResponse(
                review.getDetail(),
                reviewRequest.getRating(),
                review.getCreatedAt(),
                productId,
                user.getName()
        );
    }

    @Override
    public ReviewResponse updateReview(Long productId, ReviewRequest reviewRequest) {
        Long userId = UserUtil.getUserId();
        log.debug("리뷰 수정 요청: userId={}, productId={}, rating={}", userId, productId, reviewRequest.getRating());

        ReviewId reviewId = new ReviewId(productId, userId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("해당 리뷰가 존재하지 않습니다."));

        review.updateReview(reviewRequest.getDetail(), reviewRequest.getRating());
        log.info("리뷰 수정 성공: userId={}, productId={}, rating={}, detail='{}'",
                userId, productId, review.getRating(), review.getDetail());

        return new ReviewResponse(
                review.getDetail(),
                review.getRating(),
                review.getCreatedAt(),
                productId,
                review.getUser().getName()
        );
    }

    @Override
    public void deleteReview(Long productId) {
        Long userId = UserUtil.getUserId();
        log.debug("리뷰 삭제 요청: userId={}, productId={}", userId, productId);

        ReviewId reviewId = new ReviewId(productId, userId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("해당 리뷰가 존재하지 않습니다."));

        reviewRepository.delete(review);
        log.info("리뷰 삭제 성공: userId={}, productId={}", userId, productId);
    }
}
