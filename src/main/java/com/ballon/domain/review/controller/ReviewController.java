package com.ballon.domain.review.controller;

import com.ballon.domain.review.dto.ReviewRequest;
import com.ballon.domain.review.dto.ReviewResponse;
import com.ballon.domain.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/{product-id}/reviews")
@RequiredArgsConstructor
@Tag(name = "리뷰 API", description = "상품 리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(
            summary = "리뷰 목록 조회",
            description = "상품 ID에 해당하는 리뷰들을 페이징 및 정렬 조건으로 조회합니다. " +
                    "정렬(sort) 값은 latest(최신순), oldest(오래된순), high(평점 높은순), low(평점 낮은순) 중 선택 가능합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewResponse.class))))
    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @PathVariable("product-id") Long productId,
            @RequestParam(defaultValue = "latest") String sort,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(reviewService.getReviews(productId, sort, pageable));
    }

    @Operation(
            summary = "리뷰 작성",
            description = "해당 상품을 구매한 사용자가 리뷰를 작성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작성 성공",
                    content = @Content(schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "403", description = "구매 이력이 없는 상품"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품")
    })
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable("product-id") Long productId,
            @RequestBody ReviewRequest reviewRequest
    ) {
        return ResponseEntity.ok(reviewService.createReview(productId, reviewRequest));
    }

    @Operation(
            summary = "리뷰 수정",
            description = "사용자가 본인이 작성한 리뷰를 수정합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 리뷰가 존재하지 않음")
    })
    @PutMapping
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable("product-id") Long productId,
            @RequestBody ReviewRequest reviewRequest
    ) {
        return ResponseEntity.ok(reviewService.updateReview(productId, reviewRequest));
    }

    @Operation(
            summary = "리뷰 삭제",
            description = "사용자가 본인이 작성한 리뷰를 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 리뷰가 존재하지 않음")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteReview(
            @PathVariable("product-id") Long productId
    ) {
        reviewService.deleteReview(productId);
        return ResponseEntity.noContent().build();
    }
}
