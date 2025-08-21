package com.ballon.global.common.aop;


import com.ballon.domain.admin.entity.Admin;
import com.ballon.domain.admin.entity.Permission;
import com.ballon.domain.admin.entity.type.PermissionType;
import com.ballon.domain.admin.repository.AdminRepository;
import com.ballon.global.UserUtil;
import com.ballon.global.common.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final AdminRepository adminRepository;

    @Before("@annotation(checkPermission)")
    public void checkAdminPermission(JoinPoint joinPoint, CheckPermission checkPermission) {
        PermissionType required = checkPermission.value();

        Long currentAdminId = UserUtil.getAdminId();
        Admin admin = adminRepository.findById(currentAdminId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 관리자."));

        if (admin.isSuperAdmin()) return;

        boolean hasPermission = admin.getAdminPermissions().stream()
                .map(ap -> ap.getPermission().getName())
                .anyMatch(name -> name.equals(required.getCode()));

        if (!hasPermission) {
            throw new SecurityException("권한 부족: " + required.getCode());
        }
    }

}
