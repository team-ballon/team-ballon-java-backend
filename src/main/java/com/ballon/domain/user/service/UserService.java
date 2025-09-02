package com.ballon.domain.user.service;

import com.ballon.domain.user.dto.*;
import com.ballon.domain.user.entity.type.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserSearchResponse> search(UserSearchRequest req, Pageable pageable);

    UserResponse registerUser(UserRegisterRequest registerRequest, Role role);

    UserResponse getUserByUserId(Long userId);

    UserResponse updateUser(UserUpdateRequest userUpdateRequest, Long userId);

    void updateUserPassword(PasswordUpdateRequest passwordUpdateRequest);
}
