package com.ballon.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 클라이언트에게 Access/Refresh Token을 Json으로 보내기 위한 DTO
 */
@Getter
@AllArgsConstructor
@ToString
public class JwtResponse {
    private String accessToken;

    private String refreshToken;
}
