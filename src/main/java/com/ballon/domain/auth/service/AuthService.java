package com.ballon.domain.auth.service;

import com.ballon.domain.auth.dto.JwtResponse;
import com.ballon.domain.auth.dto.LoginRequest;
import com.ballon.domain.user.entity.User;
import com.ballon.domain.user.exception.UserNotFoundException;
import com.ballon.domain.user.repository.UserRepository;
import com.ballon.global.auth.jwt.JwtTokenUtil;
import com.ballon.global.common.exception.UnauthorizedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(LoginRequest loginRequest) {
        log.debug("로그인 요청: userId={}", loginRequest.getUserId());

        User user = userRepository.findByEmail(loginRequest.getUserId())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(loginRequest.getUserPassword(), user.getPassword())) {
            log.warn("로그인 실패 - 비밀번호 불일치: userId={}", loginRequest.getUserId());
            throw new UnauthorizedException("아이디 또는 비밀번호가 다릅니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        user.updateRefreshToken(refreshToken);
        log.info("로그인 성공: userId={}", user.getUserId());

        return new JwtResponse(accessToken, refreshToken);
    }

    public void logOut(Long userId) {
        log.debug("로그아웃 요청: userId={}", userId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(UserNotFoundException::new);

        user.updateRefreshToken(null);
        log.info("로그아웃 성공: userId={}", userId);
    }
}
