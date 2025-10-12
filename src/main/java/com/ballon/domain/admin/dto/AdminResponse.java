package com.ballon.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class AdminResponse {
    private Long adminId;
    private String email;
    private String roleName;
    private LocalDateTime createdAt;
    private List<PermissionResponse> permissions;
}

