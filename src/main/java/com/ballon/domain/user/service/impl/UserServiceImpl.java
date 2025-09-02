package com.ballon.domain.user.service.impl;

import com.ballon.domain.user.dto.*;
import com.ballon.domain.user.entity.User;
import com.ballon.domain.user.entity.type.Role;
import com.ballon.domain.user.exception.*;
import com.ballon.domain.user.repository.UserRepository;
import com.ballon.domain.user.service.UserService;
import com.ballon.global.UserUtil;
import com.ballon.global.common.exception.UnauthorizedException;
import com.ballon.global.common.response.ResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<UserSearchResponse> search(UserSearchRequest userSearchRequest, Pageable pageable) {
        log.info("사용자 조회 시도 - 검색 조건: {}, 페이지: {}", userSearchRequest, pageable);

        return userRepository.search(userSearchRequest, pageable);
    }

    @Override
    public UserResponse registerUser(UserRegisterRequest userRegisterRequest, Role role) {
        log.info("사용자 등록 시도 - 이메일: {}", userRegisterRequest.getEmail());

        if (Boolean.TRUE.equals(userRepository.existsByEmail(userRegisterRequest.getEmail()))) {
            throw new UserConflictException();
        }

        userRegisterRequest.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        User user = User.createUser(userRegisterRequest, role);
        userRepository.save(user);

        log.info("사용자 등록 완료 - 사용자 ID: {}, 이메일: {}", user.getUserId(), user.getEmail());
        return ResponseMapper.toUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUserId(Long userId) {
        log.info("사용자 조회 시도 - 사용자 ID: {}", userId);

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        log.info("사용자 조회 성공 - 사용자 ID: {}", user.getUserId());
        return ResponseMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(UserUpdateRequest userUpdateRequest, Long userId) {
        log.info("사용자 정보 수정 시도 - 사용자 ID: {}", userId);

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.updateUser(userUpdateRequest);
        userRepository.save(user);

        log.info("사용자 정보 수정 완료 - 사용자 ID: {}", user.getUserId());
        return ResponseMapper.toUserResponse(user);
    }

    @Override
    public void updateUserPassword(PasswordUpdateRequest passwordUpdateRequest) {
        Long currentUserId = UserUtil.getUserId();
        log.info("비밀번호 변경 시도 - 사용자 ID: {}", currentUserId);

        User user = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(passwordUpdateRequest.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("비밀번호 불일치");
        }

        String encodePassword = passwordEncoder.encode(passwordUpdateRequest.getNewPassword());
        user.updatePassword(encodePassword);
        userRepository.save(user);

        log.info("비밀번호 변경 완료 - 사용자 ID: {}", user.getUserId());
    }
}
