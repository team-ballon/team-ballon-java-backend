package com.ballon.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
public class UserSearchResponse {
    private Long userId; // pk
    private String email; // 이메일
    private String role; // 권한
    private LocalDateTime createdAt; // 생성 시간
}
