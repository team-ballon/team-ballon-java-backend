package com.ballon.domain.user.service;

import com.ballon.domain.user.dto.UserRegisterRequest;
import com.ballon.domain.user.dto.UserResponse;
import com.ballon.domain.user.entity.type.Role;

public interface UserService {

    /**
     * 유저를 등록합니다.
     *
     * @param registerRequest
     * @param role
     * @return
     */
    UserResponse registerUser(UserRegisterRequest registerRequest, Role role);
}
