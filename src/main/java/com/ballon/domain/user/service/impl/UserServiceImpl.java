package com.ballon.domain.user.service.impl;

import com.ballon.domain.user.dto.PasswordUpdateRequest;
import com.ballon.domain.user.dto.UserRegisterRequest;
import com.ballon.domain.user.dto.UserResponse;
import com.ballon.domain.user.dto.UserUpdateRequest;
import com.ballon.domain.user.entity.User;
import com.ballon.domain.user.entity.type.Role;
import com.ballon.domain.user.exception.*;
import com.ballon.domain.user.repository.UserRepository;
import com.ballon.domain.user.repository.VerificationCodeRepository;
import com.ballon.domain.user.service.UserService;
import com.ballon.global.UserUtil;
import com.ballon.global.common.exception.UnauthorizedException;
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
    private final UserRepository userRepository;

    @Override
    public UserResponse registerUser(UserRegisterRequest userRegisterRequest, Role role) {
        log.info("회원가입 시도: email={}, role={}", userRegisterRequest.getEmail(), role.name());

        if (Boolean.TRUE.equals(userRepository.existsByEmail(userRegisterRequest.getEmail()))) {
            throw new UserConflictException();
        }

        userRegisterRequest.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        User user = User.createUser(userRegisterRequest, role);
        userRepository.save(user);

        log.info("회원가입 성공: userId={}, email={}", user.getUserId(), user.getEmail());

        return ResponseMapper.toUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        return ResponseMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(UserUpdateRequest userUpdateRequest, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        user.updateUser(userUpdateRequest);

        userRepository.save(user);

        return ResponseMapper.toUserResponse(user);
    }

    @Override
    public void updateUserPassword(PasswordUpdateRequest passwordUpdateRequest) {
        User user = userRepository.findById(UserUtil.getUserId()).orElseThrow(UserNotFoundException::new);

        if(!passwordEncoder.matches(passwordUpdateRequest.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("비밀번호 불일치");
        }

        String encodePassword = passwordEncoder.encode(passwordUpdateRequest.getNewPassword());
        user.updatePassword(encodePassword);

        userRepository.save(user);
    }
}
