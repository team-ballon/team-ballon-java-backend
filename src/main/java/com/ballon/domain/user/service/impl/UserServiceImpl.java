package com.ballon.domain.user.service.impl;

import com.ballon.domain.auth.repository.RefreshTokenRepository;
import com.ballon.domain.user.dto.UserRegisterRequest;
import com.ballon.domain.user.dto.UserResponse;
import com.ballon.domain.user.entity.User;
import com.ballon.domain.user.entity.type.Role;
import com.ballon.domain.user.exception.*;
import com.ballon.domain.user.repository.UserRepository;
import com.ballon.domain.user.service.UserService;
import com.ballon.global.common.response.ResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    @Override
    public UserResponse registerUser(UserRegisterRequest userRegisterRequest, Role role) {
        log.info("회원가입 시도: email={}, role={}", userRegisterRequest.getEmail(), role.name());

        if (Boolean.TRUE.equals(userRepository.existsByEmailAndIsActiveIsTrue(userRegisterRequest.getEmail()))) {
            throw new UserConflictException();
        }

        userRegisterRequest.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        User user = User.createUser(userRegisterRequest, role);
        userRepository.save(user);

        log.info("회원가입 성공: userId={}, email={}", user.getUserId(), user.getEmail());

        return ResponseMapper.toUserResponse(user);
    }

}
