package com.ballon.domain.admin.dto;

import com.ballon.domain.user.dto.UserRegisterRequest;
import lombok.Getter;

import java.util.List;

@Getter
public class AdminRequest {
    UserRegisterRequest userRegisterRequest;
    private String email;

    private String password;

    private String roleName;

    private List<Long> permissionIds;
}
