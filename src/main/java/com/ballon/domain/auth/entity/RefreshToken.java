package com.ballon.domain.auth.entity;

import com.ballon.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * 토큰을 DB에 저장
 * 사용자별로 토큰을 저장/검증
 */
@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public final class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long refreshTokenId;

    @Column(length = 200, nullable = false)
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static RefreshToken createRefreshToken(String refreshToken, User user) {
        return new RefreshToken(null, refreshToken, user);
    }

    public void updateToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
