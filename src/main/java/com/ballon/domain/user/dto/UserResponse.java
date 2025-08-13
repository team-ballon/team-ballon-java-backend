package com.ballon.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class UserResponse {

    // 아이디
    private Long userId;


    // 이름
    private String name;


    // 권한
    private String role;

    // 생성 시간
    private LocalDateTime createdAt;
}
