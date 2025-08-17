package com.ballon.domain.admin.service.impl;

import com.ballon.domain.admin.dto.PermissionRequest;
import com.ballon.domain.admin.dto.PermissionResponse;
import com.ballon.domain.admin.entity.Permission;
import com.ballon.domain.admin.repository.PermissionRepository;
import com.ballon.domain.admin.service.PermissionService;
import com.ballon.global.common.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionResponse createPermission(PermissionRequest permissionRequest) {
        if(permissionRepository.existsByName(permissionRequest.getPermissionName())) {
            throw new ConflictException("이미 존재하는 권한입니다.");
        }

        Permission permission = Permission.of(
                permissionRequest.getPermissionName(),
                permissionRequest.getPermissionDescription()
        );

        permissionRepository.save(permission);

        return new PermissionResponse(
                permission.getPermissionId(),
                permissionRequest.getPermissionName(),
                permissionRequest.getPermissionDescription()
        );
    }
}
