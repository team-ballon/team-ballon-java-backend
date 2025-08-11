package com.ballon.domain.auth.service;

import com.ballon.domain.auth.dto.JwtResponse;
import com.ballon.domain.auth.dto.LoginRequest;
import com.ballon.domain.auth.entity.RefreshToken;
import com.ballon.domain.auth.repository.RefreshTokenRepository;
import com.ballon.domain.user.entity.User;
import com.ballon.domain.user.entity.type.Role;
import com.ballon.domain.user.exception.UserNotFoundException;
import com.ballon.domain.user.repository.UserRepository;
import com.ballon.global.auth.jwt.JwtTokenUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmailAndIsActiveIsTrue(loginRequest.getUserId())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(loginRequest.getUserPassword(), user.getPassword())) {
            throw new RuntimeException();
        }

        String accessToken;
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        if (Role.TRAINER == user.getRole()) {
            Long trainerId = trainerRepository.findTrainerIdByUserId(user.getUserId())
                    .orElseThrow(UserNotFoundException::new);

            accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), trainerId);
        } else {
            accessToken = jwtTokenProvider.createAccessToken(user.getUserId());
        }

        // 기존 refresh token 조회
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser_UserId(user.getUserId());

        if (existingToken.isPresent()) {
            existingToken.get().updateToken(refreshToken); // setter 또는 별도 메서드 사용
        } else {
            refreshTokenRepository.save(RefreshToken.createRefreshToken(refreshToken, user));
        }

        return new JwtResponse(accessToken, refreshToken);
    }


    public void logOut(Long userId) {
        if (Boolean.FALSE.equals(userRepository.existsByUserId(userId))) {
            throw new UserNotFoundException();
        }

        refreshTokenRepository.deleteByUser_UserId(userId);
    }
}
