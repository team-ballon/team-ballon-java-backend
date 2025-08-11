package com.ballon.global.auth.jwt;

import com.fightingkorea.platform.domain.auth.repository.RefreshTokenRepository;
import com.fightingkorea.platform.domain.trainer.repository.TrainerRepository;
import com.fightingkorea.platform.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 30L;            // 30분
    private static final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7L;  // 7일

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;

    @Value("${jwt.access.secret.key}")
    private String accessSecret;

    @Value("${jwt.refresh.secret.key}")
    private String refreshSecret;

    // access token 생성 (trainerId 없이)
    public String createAccessToken(Long userId) {
        return createToken(userId, ACCESS_TOKEN_VALIDITY, accessSecret, null);
    }

    // access token 생성 (trainerId 포함)
    public String createAccessToken(Long userId, Long trainerId) {
        return createToken(userId, ACCESS_TOKEN_VALIDITY, accessSecret, trainerId);
    }

    // refresh token 생성
    public String createRefreshToken(Long userId) {
        return createToken(userId, REFRESH_TOKEN_VALIDITY, refreshSecret, null);
    }

    // 공통 토큰 생성 로직
    private String createToken(Long userId, long validity, String secret, Long trainerId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        if (trainerId != null) {
            claims.put("trainerId", trainerId);
        }

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

    // trainerId 추출 (access token에서만 사용)
    public Long getTrainerId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object trainerId = claims.get("trainerId");
        return trainerId != null ? Long.parseLong(trainerId.toString()) : null;
    }

    // refresh token으로 access token 재발급
    public String refresh(String refreshToken) {
        if (!validateToken(refreshToken, false)) {
            throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
        }

        Long userId = Long.parseLong(getUserId(refreshToken, false));

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        String storedToken = refreshTokenRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Refresh Token이 존재하지 않습니다."))
                .getRefreshToken();

        if (!refreshToken.equals(storedToken)) {
            throw new RuntimeException("Refresh Token이 일치하지 않습니다.");
        }

        // trainerId 조회 (선택적으로 존재)
        Long trainerId = trainerRepository.findTrainerIdByUserId(userId).orElse(null);

        return Objects.nonNull(trainerId) ?
                createAccessToken(userId, trainerId) : createAccessToken(userId);
    }

}
