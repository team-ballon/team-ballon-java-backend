package com.ballon.global.common.aop;

import com.ballon.domain.admin.entity.Admin;
import com.ballon.domain.admin.repository.AdminRepository;
import com.ballon.global.UserUtil;
import com.ballon.global.common.exception.ForbiddenException;
import com.ballon.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class SuperAdminAspect {

    private final AdminRepository adminRepository;

    @Before("@annotation(com.ballon.global.common.aop.CheckSuperAdmin)")
    public void checkSuperAdmin(JoinPoint joinPoint) {
        Long currentAdminId = UserUtil.getAdminId();

        Admin admin = adminRepository.findById(currentAdminId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 관리자"));

        if (!admin.isSuperAdmin()) {
            throw new ForbiddenException("슈퍼 관리자가 아닙니다.");
        }
    }
}

