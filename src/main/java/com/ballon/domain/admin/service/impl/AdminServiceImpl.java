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
import com.ballon.global.UserUtil;
import com.ballon.global.common.exception.ForbiddenException;
import com.ballon.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public AdminResponse getAdminByAdminId(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 관리자."));

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

    @Transactional(readOnly = true)
    @Override
    public Page<AdminResponse> searchAdmins(AdminSearchRequest req, Pageable pageable) {
        Sort sort = "oldest".equals(req.getSort())
                ? Sort.by("createdAt").ascending()
                : Sort.by("createdAt").descending();

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        return adminRepository.search(req, sortedPageable);
    }


    @Override
    public AdminResponse createAdmin(AdminRequest adminRequest) {
        // 유저 생성
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

        return new AdminResponse(
                admin.getAdminId(),
                user.getEmail(),
                admin.getRole(),
                permissionService.assignPermission(adminRequest.getPermissionIds(), admin)
        );
    }

    @Override
    public AdminResponse updateAdmin(Long adminId, AdminUpdateRequest adminUpdateRequest) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 관리자."));

        admin.updateRole(adminUpdateRequest.getRoleName());

        admin.getAdminPermissions().clear();

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

        adminRepository.save(admin);

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

    @Override
    public void removeAdmin(Long adminId) {
        if(!adminRepository.existsById(adminId)) {
            throw new NotFoundException("존재하지 않는 관리자.");
        }

        Admin admin = adminRepository.getReferenceById(adminId);

        adminRepository.delete(admin);
    }
}
