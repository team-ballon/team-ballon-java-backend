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
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomAdminRepositoryImpl implements CustomAdminRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminResponse> search(AdminSearchRequest req, Pageable pageable) {
        QAdmin admin = QAdmin.admin;
        QAdminPermission adminPermission = QAdminPermission.adminPermission;

        // --- WHERE 조건 생성 ---
        BooleanBuilder builder = new BooleanBuilder();
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            builder.and(admin.user.email.containsIgnoreCase(req.getEmail()));
        }
        if (req.getRole() != null && !req.getRole().isBlank()) {
            builder.and(admin.role.eq(req.getRole()));
        }

        boolean hasPermissionFilter = req.getPermissionIds() != null && !req.getPermissionIds().isEmpty();
        if (hasPermissionFilter) {
            builder.and(adminPermission.permission.permissionId.in(req.getPermissionIds()));
        }

        // --- 카운트 쿼리 (Count Query) ---
        JPAQuery<Long> countQuery = queryFactory
                .select(admin.countDistinct())
                .from(admin);

        if (hasPermissionFilter) {
            countQuery.innerJoin(admin.adminPermissions, adminPermission)
                    .innerJoin(adminPermission.permission);
        }
        Long total = countQuery.where(builder).fetchOne();

        if (total == null || total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // --- 1단계: 대상 ID 목록 조회 (fetchJoin 없이 DB에서 페이징) ---
        JPAQuery<Long> idsQuery = queryFactory
                .select(admin.adminId)
                .from(admin);

        if (hasPermissionFilter) {
            idsQuery.innerJoin(admin.adminPermissions, adminPermission)
                    .innerJoin(adminPermission.permission);
        }

        List<Long> ids = idsQuery.where(builder)
                .orderBy(getOrderSpecifier(req.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (ids.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, total);
        }

        // --- 2단계: 조회된 ID로 실제 콘텐츠 조회 (fetchJoin으로 N+1 문제 해결) ---
        List<Admin> admins = queryFactory
                .selectFrom(admin)
                .leftJoin(admin.adminPermissions, adminPermission).fetchJoin()
                .leftJoin(adminPermission.permission).fetchJoin()
                .where(admin.adminId.in(ids))
                .orderBy(getOrderSpecifier(req.getSort()))
                .fetch();

        List<AdminResponse> content = admins.stream()
                .map(ResponseMapper::toAdminResponse)
                .toList();

        return new PageImpl<>(content, pageable, total);
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