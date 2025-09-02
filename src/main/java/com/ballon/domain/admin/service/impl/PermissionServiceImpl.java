package com.ballon.domain.admin.service.impl;

import com.ballon.domain.admin.dto.PermissionResponse;
import com.ballon.domain.admin.entity.Admin;
import com.ballon.domain.admin.entity.AdminPermission;
import com.ballon.domain.admin.entity.Permission;
import com.ballon.domain.admin.entity.id.AdminPermissionId;
import com.ballon.domain.admin.repository.AdminPermissionRepository;
import com.ballon.domain.admin.repository.PermissionRepository;
import com.ballon.domain.admin.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final AdminPermissionRepository adminPermissionRepository;

    @Transactional(readOnly = true)
    @Override
    public List<PermissionResponse> findAllPermissions() {
        log.info("findAllPermissions 호출");

        List<PermissionResponse> responses = permissionRepository.findAll().stream()
                .map(p -> new PermissionResponse(
                        p.getPermissionId(),
                        p.getName(),
                        p.getDescription()
                ))
                .toList();

        log.info("전체 권한 조회 완료 - 총 {}건", responses.size());
        return responses;
    }

    @Override
    public List<PermissionResponse> assignPermission(List<Long> permissionIds, Admin admin) {
        log.info("assignPermission 호출 - adminId: {}, 요청 권한 수: {}", admin.getAdminId(), permissionIds.size());

        List<AdminPermission> adminPermissions = new ArrayList<>();

        permissionIds.forEach(permissionId -> {
            Permission permission = permissionRepository.getReferenceById(permissionId);

            AdminPermissionId adminPermissionId = new AdminPermissionId(
                    admin.getAdminId(),
                    permission.getPermissionId()
            );

            AdminPermission adminPermission = new AdminPermission(adminPermissionId, admin, permission);
            adminPermissions.add(adminPermission);

            log.debug("권한 연결 준비 완료 - adminId: {}, permissionId: {}", admin.getAdminId(), permission.getPermissionId());
        });

        adminPermissionRepository.saveAll(adminPermissions);
        log.info("관리자 권한 저장 완료 - adminId: {}, 저장된 권한 수: {}", admin.getAdminId(), adminPermissions.size());

        List<PermissionResponse> responses = adminPermissions.stream()
                .map(ap -> new PermissionResponse(
                        ap.getPermission().getPermissionId(),
                        ap.getPermission().getName(),
                        ap.getPermission().getDescription()
                ))
                .toList();

        log.info("권한 할당 완료 - adminId: {}, 반환된 권한 수: {}", admin.getAdminId(), responses.size());
        return responses;
    }
}
