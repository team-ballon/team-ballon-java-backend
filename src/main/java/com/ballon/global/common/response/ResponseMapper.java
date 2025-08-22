package com.ballon.global.common.response;

import com.ballon.domain.admin.dto.AdminResponse;
import com.ballon.domain.admin.dto.PermissionResponse;
import com.ballon.domain.admin.entity.Admin;
import com.ballon.domain.user.dto.UserResponse;
import com.ballon.domain.user.entity.User;

public class ResponseMapper {

    private ResponseMapper() {}

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(user.getUserId(), user.getEmail(),user.getName(), user.getAge(), user.getSex().getLabel(),
                user.getRole().getLabel(), user.getCreatedAt());
    }

    public static AdminResponse toAdminResponse(Admin admin) {
        return new AdminResponse(
                admin.getAdminId(),
                admin.getUser().getEmail(),
                admin.getRole(),
                admin.getAdminPermissions().stream()
                        .map(ap -> new PermissionResponse(
                                ap.getPermission().getPermissionId(),
                                ap.getPermission().getName(),
                                ap.getPermission().getDescription()
                        ))
                        .toList()
        );
    }
}
