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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey; // SecretKey 타입 사용 권장
import java.util.Date;

@Slf4j
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

    private String maskToken(String token) {
        return token.substring(0, 8) + "...";
    }

    private String createToken(Long userId, long validity, String secretBase64) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));

        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("토큰 생성: userId={}, 만료시간={}, token[{}]", userId, expiry, maskToken(token));
        return token;
    }

    public boolean validateToken(String token, boolean isAccess) {
        try {
            String secretBase64 = isAccess ? accessSecret : refreshSecret;
            byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date now = new Date();
            Date expiry = claims.getExpiration();
            long remainingMillis = expiry.getTime() - now.getTime();

            log.info("토큰 검증 성공: userId={}, 현재시간={}, 만료시간={}, 남은시간={}ms, token[{}]",
                    claims.getSubject(), now, expiry, remainingMillis, maskToken(token));

            if (remainingMillis <= 0) {
                log.warn("토큰 만료됨: userId={}, token[{}]", claims.getSubject(), maskToken(token));
                return false;
            }

            return true;
        } catch (JwtException e) {
            log.warn("토큰 검증 실패: 이유={}, token[{}]", e.getMessage(), maskToken(token));
            return false;
        }
    }

    public String getUserId(String token, boolean isAccess) {
        String secretBase64 = isAccess ? accessSecret : refreshSecret;
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String userId = claims.getSubject();
        log.debug("토큰에서 userId 추출: userId={}, token[{}]", userId, maskToken(token));
        return userId;
    }

    public String createAccessToken(Long userId) {
        return createToken(userId, ACCESS_TOKEN_VALIDITY, accessSecret);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, REFRESH_TOKEN_VALIDITY, refreshSecret);
    }

    public String refresh(String refreshToken) {
        log.info("Refresh 요청: token[{}]", maskToken(refreshToken));

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

        String newAccessToken = createAccessToken(userId);
        log.info("Access Token 재발급: userId={}, token[{}]", userId, maskToken(newAccessToken));

        return newAccessToken;
    }
}
