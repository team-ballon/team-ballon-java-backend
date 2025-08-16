package com.ballon.domain.user.service;

import com.ballon.domain.user.dto.PasswordUpdateRequest;
import com.ballon.domain.user.dto.UserRegisterRequest;
import com.ballon.domain.user.dto.UserResponse;
import com.ballon.domain.user.dto.UserUpdateRequest;
import com.ballon.domain.user.entity.type.Role;

public interface UserService {

    UserResponse registerUser(UserRegisterRequest registerRequest, Role role);

    UserResponse getUserByUserId(Long userId);

    UserResponse updateUser(UserUpdateRequest userUpdateRequest, Long userId);

    void updateUserPassword(PasswordUpdateRequest passwordUpdateRequest);
}
