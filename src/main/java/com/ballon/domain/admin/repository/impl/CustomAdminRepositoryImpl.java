package com.ballon.domain.admin.repository.impl;

import com.ballon.domain.admin.dto.AdminResponse;
import com.ballon.domain.admin.dto.AdminSearchRequest;
import com.ballon.domain.admin.entity.Admin;
import com.ballon.domain.admin.entity.QAdmin;
import com.ballon.domain.admin.entity.QAdminPermission;
import com.ballon.domain.admin.repository.CustomAdminRepository;
import com.ballon.global.common.response.ResponseMapper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomAdminRepositoryImpl implements CustomAdminRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminResponse> search(AdminSearchRequest req, Pageable pageable) {
        QAdmin admin = QAdmin.admin;
        QAdminPermission adminPermission = QAdminPermission.adminPermission;

        BooleanBuilder builder = new BooleanBuilder();

        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            builder.and(admin.user.email.containsIgnoreCase(req.getEmail()));
        }

        if (req.getRole() != null && !req.getRole().isBlank()) {
            builder.and(admin.role.eq(req.getRole()));
        }

        if (req.getPermissionIds() != null && !req.getPermissionIds().isEmpty()) {
            builder.and(adminPermission.permission.permissionId.in(req.getPermissionIds()));
        }

        // 1. Content 조회 (올바른 조인 사용)
        List<Admin> admins = queryFactory
                .selectFrom(admin)
                .leftJoin(admin.adminPermissions, adminPermission).fetchJoin()
                .leftJoin(adminPermission.permission).fetchJoin() // <--- 이 조인이 반드시 필요합니다.
                .where(builder)
                .distinct()
                .orderBy(getOrderSpecifier(req.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<AdminResponse> content = admins.stream()
                .map(ResponseMapper::toAdminResponse)
                .toList();


        // 2. Count 조회 (올바른 조인과 countDistinct 사용)
        Long total = queryFactory
                .select(admin.countDistinct()) // <--- countDistinct()로 변경
                .from(admin)
                .leftJoin(admin.adminPermissions, adminPermission)
                .leftJoin(adminPermission.permission) // <--- Content 쿼리와 동일한 조인 추가
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort) {
        QAdmin admin = QAdmin.admin;

        if (sort == null) {
            return admin.createdAt.desc();
        }

        return switch (sort.toLowerCase()) {
            case "oldest" -> admin.createdAt.asc();
            case "role" -> admin.role.asc();
            case "email" -> admin.user.email.asc();
            default -> admin.createdAt.desc();
        };
    }
}
