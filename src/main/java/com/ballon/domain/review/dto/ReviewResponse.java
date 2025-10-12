package com.ballon.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
public class ReviewResponse {
    private String detail;
    private int rating;
    private LocalDateTime createdAt;
    private Long productId;
    private String userName;
}
