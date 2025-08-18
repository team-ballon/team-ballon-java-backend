package com.ballon.domain.admin.service.impl;

import com.ballon.domain.admin.dto.AdminRequest;
import com.ballon.domain.admin.dto.AdminResponse;
import com.ballon.domain.admin.entity.Admin;
import com.ballon.domain.admin.entity.AdminPermission;
import com.ballon.domain.admin.entity.Permission;
import com.ballon.domain.admin.entity.id.AdminPermissionId;
import com.ballon.domain.admin.repository.AdminPermissionRepository;
import com.ballon.domain.admin.repository.AdminRepository;
import com.ballon.domain.admin.repository.PermissionRepository;
import com.ballon.domain.admin.service.AdminService;
import com.ballon.domain.user.dto.UserRegisterRequest;
import com.ballon.domain.user.dto.UserResponse;
import com.ballon.domain.user.entity.User;
import com.ballon.domain.user.entity.type.Role;
import com.ballon.domain.user.entity.type.Sex;
import com.ballon.domain.user.repository.UserRepository;
import com.ballon.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final AdminPermissionRepository adminPermissionRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public AdminResponse createAdmin(AdminRequest adminRequest) {
        // 유저 생성
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                adminRequest.getEmail(),
                adminRequest.getPassword(),
                20,
                Sex.MALE,
                UUID.randomUUID().toString()
        );

        UserResponse userResponse = userService.registerUser(userRegisterRequest, Role.ADMIN);
        User user = userRepository.getReferenceById(userResponse.getUserId());

        // 어드민 생성
        Admin admin = Admin.of(user, adminRequest.getRoleName());

        adminRepository.save(admin);

        // 어드민 - 권한 연결
        List<AdminPermission> adminPermissions = new ArrayList<>();

        adminRequest.getPermissionIds().forEach(permissionId -> {
            Permission permission = permissionRepository.getReferenceById(permissionId);

            AdminPermissionId adminPermissionId = new AdminPermissionId(
                    admin.getAdminId(),
                    permission.getPermissionId()
            );

            AdminPermission adminPermission = new AdminPermission(adminPermissionId, admin, permission);

            adminPermissions.add(adminPermission);
        });

        adminPermissionRepository.saveAll(adminPermissions);

        return new AdminResponse(
                admin.getAdminId(),
                user.getEmail(),
                admin.getRole(),
                adminPermissions.stream()
                        .map(ap -> ap.getPermission().getName())
                        .toList()
        );
    }

}
