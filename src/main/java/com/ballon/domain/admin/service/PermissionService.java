package com.ballon.domain.admin.service;

import com.ballon.domain.admin.dto.PermissionResponse;
import com.ballon.domain.admin.entity.Admin;

import java.util.List;

public interface PermissionService {
    List<PermissionResponse> findAllPermissions();

    List<PermissionResponse> assignPermission(List<Long> permissionIds, Admin admin);
}
