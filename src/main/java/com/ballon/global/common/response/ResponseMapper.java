package com.ballon.global.common.response;

import com.ballon.domain.user.dto.UserResponse;
import com.ballon.domain.user.entity.User;

public class ResponseMapper {

    private ResponseMapper() {}

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(user.getUserId(), user.getEmail(),user.getName(), user.getSex().getLabel(),
                user.getRole().getLabel(), user.getCreatedAt());
    }
}
