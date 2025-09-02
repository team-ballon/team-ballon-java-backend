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
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAQuery; // JPAQuery 임포트
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

        boolean hasPermissionFilter = req.getPermissionIds() != null && !req.getPermissionIds().isEmpty();
        if (hasPermissionFilter) {
            builder.and(adminPermission.permission.permissionId.in(req.getPermissionIds()));
        }

        JPAQuery<Admin> contentQuery = queryFactory
                .selectFrom(admin)
                .distinct();

        JPAQuery<Long> countQuery = queryFactory
                .select(admin.countDistinct())
                .from(admin);

        if (hasPermissionFilter) {
            contentQuery
                    .innerJoin(admin.adminPermissions, adminPermission).fetchJoin()
                    .innerJoin(adminPermission.permission).fetchJoin();

            countQuery
                    .innerJoin(admin.adminPermissions, adminPermission)
                    .innerJoin(adminPermission.permission);
        } else {
            contentQuery
                    .leftJoin(admin.adminPermissions, adminPermission).fetchJoin()
                    .leftJoin(adminPermission.permission).fetchJoin();
        }

        // 4. 쿼리 실행
        List<Admin> admins = contentQuery
                .where(builder)
                .orderBy(getOrderSpecifier(req.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = countQuery.where(builder).fetchOne();

        List<AdminResponse> content = admins.stream()
                .map(ResponseMapper::toAdminResponse)
                .toList();

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
