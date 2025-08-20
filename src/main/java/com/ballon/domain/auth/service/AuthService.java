package com.ballon.domain.auth.service;

import com.ballon.domain.auth.dto.JwtResponse;
import com.ballon.domain.auth.dto.LoginRequest;
import com.ballon.domain.partner.repository.PartnerRepository;
import com.ballon.domain.user.entity.User;
import com.ballon.domain.user.entity.type.Role;
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
    private final PartnerRepository partnerRepository;
    private final JwtTokenUtil jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getUserId())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(loginRequest.getUserPassword(), user.getPassword())) {
            throw new UnauthorizedException("아이디 또는 비밀번호가 다릅니다.");
        }

        String accessToken;
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        if (Role.PARTNER == user.getRole()) {
            Long trainerId = partnerRepository.findPartnerIdByUserId(user.getUserId())
                    .orElseThrow(UserNotFoundException::new);

            accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), trainerId);
        } else {
            accessToken = jwtTokenProvider.createAccessToken(user.getUserId());
        }

        user.updateRefreshToken(refreshToken);

        return new JwtResponse(accessToken, refreshToken);
    }


    public void logOut(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(UserNotFoundException::new);

        user.updateRefreshToken(null);
    }
}
