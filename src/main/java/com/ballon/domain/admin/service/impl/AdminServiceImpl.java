package com.ballon.domain.admin.service.impl;

import com.ballon.domain.admin.dto.*;
import com.ballon.domain.admin.entity.Admin;
import com.ballon.domain.admin.entity.AdminPermission;
import com.ballon.domain.admin.entity.Permission;
import com.ballon.domain.admin.entity.id.AdminPermissionId;
import com.ballon.domain.admin.repository.AdminRepository;
import com.ballon.domain.admin.repository.PermissionRepository;
import com.ballon.domain.admin.service.AdminService;
import com.ballon.domain.admin.service.PermissionService;
import com.ballon.domain.user.dto.UserRegisterRequest;
import com.ballon.domain.user.dto.UserResponse;
import com.ballon.domain.user.entity.User;
import com.ballon.domain.user.entity.type.Role;
import com.ballon.domain.user.entity.type.Sex;
import com.ballon.domain.user.repository.UserRepository;
import com.ballon.domain.user.service.UserService;
import com.ballon.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public AdminResponse getAdminByAdminId(Long adminId) {
        log.info("getAdminByAdminId 호출 - adminId: {}", adminId);

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 관리자."));

        AdminResponse response = new AdminResponse(
                admin.getAdminId(),
                admin.getUser().getEmail(),
                admin.getRole(),
                admin.getCreatedAt(),
                admin.getAdminPermissions().stream()
                        .map(ap -> new PermissionResponse(
                                ap.getPermission().getPermissionId(),
                                ap.getPermission().getName(),
                                ap.getPermission().getDescription()
                        ))
                        .toList()
        );

        log.info("관리자 조회 성공 - adminId: {}, email: {}", adminId, admin.getUser().getEmail());
        return response;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<AdminResponse> searchAdmins(AdminSearchRequest req, Pageable pageable) {
        log.info("searchAdmins 호출 - 검색 조건: {}, 페이지: {}", req, pageable);

        return adminRepository.search(req, pageable);
    }

    @Override
    public AdminResponse createAdmin(AdminRequest adminRequest) {
        log.info("createAdmin 호출 - email: {}", adminRequest.getEmail());

        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                adminRequest.getEmail(),
                adminRequest.getPassword(),
                2,
                Sex.MALE,
                UUID.randomUUID().toString()
        );

        UserResponse userResponse = userService.registerUser(userRegisterRequest, Role.ADMIN);
        User user = userRepository.getReferenceById(userResponse.getUserId());

        Admin admin = Admin.of(user, adminRequest.getRoleName());
        adminRepository.save(admin);

        AdminResponse response = new AdminResponse(
                admin.getAdminId(),
                user.getEmail(),
                admin.getRole(),
                admin.getCreatedAt(),
                permissionService.assignPermission(adminRequest.getPermissionIds(), admin)
        );

        log.info("관리자 생성 완료 - adminId: {}, email: {}", admin.getAdminId(), user.getEmail());
        return response;
    }

    @Override
    @Transactional
    public AdminResponse updateAdmin(Long adminId, AdminUpdateRequest adminUpdateRequest) {
        log.info("updateAdmin 호출 - adminId: {}, 요청: {}", adminId, adminUpdateRequest);

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 관리자."));

        admin.updateRole(adminUpdateRequest.getRoleName());
        log.debug("관리자 역할 업데이트 완료 - adminId: {}, role: {}", adminId, adminUpdateRequest.getRoleName());

        admin.getAdminPermissions().removeIf(ap -> true);
        adminRepository.flush();
        log.debug("기존 권한 제거 완료 - adminId: {}", adminId);

        List<Permission> permissions = permissionRepository.findAllById(adminUpdateRequest.getPermissionIds());
        for (Permission permission : permissions) {
            admin.getAdminPermissions().add(
                    new AdminPermission(
                            new AdminPermissionId(admin.getAdminId(), permission.getPermissionId()),
                            admin,
                            permission
                    )
            );
        }
        log.debug("새로운 권한 추가 완료 - adminId: {}, permissions: {}", adminId, permissions.size());

        AdminResponse response = new AdminResponse(
                admin.getAdminId(),
                admin.getUser().getEmail(),
                admin.getRole(),
                admin.getCreatedAt(),
                admin.getAdminPermissions().stream()
                        .map(ap -> new PermissionResponse(
                                ap.getPermission().getPermissionId(),
                                ap.getPermission().getName(),
                                ap.getPermission().getDescription()
                        ))
                        .toList()
        );

        log.info("관리자 업데이트 완료 - adminId: {}", adminId);
        return response;
    }

    @Override
    public void removeAdminByAdminId(Long adminId) {
        log.info("removeAdminByAdminId 호출 - adminId: {}", adminId);

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 관리자."));

        adminRepository.delete(admin);
        userRepository.deleteById(admin.getUser().getUserId());

        log.info("관리자 및 유저 삭제 완료 - adminId: {}, userId: {}", adminId, admin.getUser().getUserId());
    }
}
