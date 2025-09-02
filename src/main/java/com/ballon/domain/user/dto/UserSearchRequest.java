package com.ballon.domain.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSearchRequest {
    private String email;    // 이메일 검색
    private String name;     // 이름 검색
    private Integer minAge;  // 최소 나이
    private Integer maxAge;  // 최대 나이
    private String sex;      // 성별 (MALE/FEMALE)
    private String role;     // 권한 (USER/PARTNER/ADMIN)
    private String sort;     // 정렬 조건
}
