package com.ballon.global.auth.jwt;

import com.ballon.domain.user.exception.UserNotFoundException;
import com.ballon.domain.user.repository.UserRepository;
import com.ballon.global.common.exception.NotFoundException;
import com.ballon.global.common.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders; // Base64 디코더 import
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey; // SecretKey 타입 사용 권장
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 30L;            // 30분
    private static final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7L;  // 7일

    private final UserRepository userRepository;

    @Value("${jwt.access.secret.key}")
    private String accessSecret;

    @Value("${jwt.refresh.secret.key}")
    private String refreshSecret;

    // access token 생성
    public String createAccessToken(Long userId) {
        return createToken(userId, ACCESS_TOKEN_VALIDITY, accessSecret);
    }

    // refresh token 생성
    public String createRefreshToken(Long userId) {
        return createToken(userId, REFRESH_TOKEN_VALIDITY, refreshSecret);
    }

    // [수정됨] 공통 토큰 생성 로직
    private String createToken(Long userId, long validity, String secretBase64) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));

        // 1. Base64로 인코딩된 secret 문자열을 원본 바이트 배열로 디코딩합니다.
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        // 2. 디코딩된 바이트 배열로부터 안전한 SecretKey 객체를 생성합니다.
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256) // 생성된 key 객체로 서명
                .compact();
    }

    // [수정됨] 토큰 유효성 검증
    public boolean validateToken(String token, boolean isAccess) {
        try {
            String secretBase64 = isAccess ? accessSecret : refreshSecret;
            byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // [수정됨] userId 추출
    public String getUserId(String token, boolean isAccess) {
        String secretBase64 = isAccess ? accessSecret : refreshSecret;
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // refresh token으로 access token 재발급 (내부 로직은 변경 없음)
    public String refresh(String refreshToken) {
        if (!validateToken(refreshToken, false)) {
            throw new UnauthorizedException("유효하지 않은 Refresh Token입니다.");
        }

        Long userId = Long.parseLong(getUserId(refreshToken, false));

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        String storedToken = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Refresh Token이 존재하지 않습니다."))
                .getRefreshToken();

        if (!refreshToken.equals(storedToken)) {
            throw new UnauthorizedException("Refresh Token이 일치하지 않습니다.");
        }

        return createAccessToken(userId);
    }
}

