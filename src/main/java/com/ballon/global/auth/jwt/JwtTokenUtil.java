package com.ballon.global.auth.jwt;

import com.ballon.domain.user.exception.UserNotFoundException;
import com.ballon.domain.user.repository.UserRepository;
import com.ballon.global.common.exception.NotFoundException;
import com.ballon.global.common.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    // 공통 토큰 생성 로직
    private String createToken(Long userId, long validity, String secret) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token, boolean isAccess) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey((isAccess ? accessSecret : refreshSecret).getBytes())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // userId 추출
    public String getUserId(String token, boolean isAccess) {
        return Jwts.parserBuilder()
                .setSigningKey((isAccess ? accessSecret : refreshSecret).getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // refresh token으로 access token 재발급
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
