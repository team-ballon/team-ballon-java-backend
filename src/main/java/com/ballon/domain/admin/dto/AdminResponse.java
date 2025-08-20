package com.ballon.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AdminResponse {
    private Long adminId;
    private String email;
    private String roleName;
    private List<PermissionResponse> permissions;
}

