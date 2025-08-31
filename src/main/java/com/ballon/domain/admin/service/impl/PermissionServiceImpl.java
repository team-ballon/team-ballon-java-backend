package com.ballon.domain.admin.service.impl;

import com.ballon.domain.admin.dto.PermissionRequest;
import com.ballon.domain.admin.dto.PermissionResponse;
import com.ballon.domain.admin.entity.Admin;
import com.ballon.domain.admin.entity.AdminPermission;
import com.ballon.domain.admin.entity.Permission;
import com.ballon.domain.admin.entity.id.AdminPermissionId;
import com.ballon.domain.admin.repository.AdminPermissionRepository;
import com.ballon.domain.admin.repository.PermissionRepository;
import com.ballon.domain.admin.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final AdminPermissionRepository adminPermissionRepository;

    @Transactional(readOnly = true)
    @Override
    public List<PermissionResponse> findAllPermissions() {
        return permissionRepository.findAll().stream().map(p -> new PermissionResponse(
                p.getPermissionId(),
                p.getName(),
                p.getDescription()
        )).toList();
    }

    @Override
    public List<PermissionResponse> assignPermission(List<Long> permissionIds, Admin admin) {
        // 어드민 - 권한 연결
        List<AdminPermission> adminPermissions = new ArrayList<>();

        permissionIds.forEach(permissionId -> {
            Permission permission = permissionRepository.getReferenceById(permissionId);

            AdminPermissionId adminPermissionId = new AdminPermissionId(
                    admin.getAdminId(),
                    permission.getPermissionId()
            );

            AdminPermission adminPermission = new AdminPermission(adminPermissionId, admin, permission);

            adminPermissions.add(adminPermission);
        });

        adminPermissionRepository.saveAll(adminPermissions);

        return adminPermissions.stream()
                .map(ap -> new PermissionResponse(
                        ap.getPermission().getPermissionId(),
                        ap.getPermission().getName(),
                        ap.getPermission().getDescription()
                ))
                .toList();
    }
}
