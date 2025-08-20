package com.ballon.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long userId; // pk
    private String email; // 이메일
    private String name; // 이름
    private int age; // 나이
    private String sex; // 성별
    private String role; // 권한
    private LocalDateTime createdAt; // 생성 시간
}
