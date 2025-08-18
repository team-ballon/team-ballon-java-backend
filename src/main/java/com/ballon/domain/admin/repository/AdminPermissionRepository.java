package com.ballon.domain.admin.repository;

import com.ballon.domain.admin.entity.AdminPermission;
import com.ballon.domain.admin.entity.id.AdminPermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminPermissionRepository extends JpaRepository<AdminPermission, AdminPermissionId> {
}
