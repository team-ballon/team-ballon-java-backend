package com.ballon.domain.admin.repository;

import com.ballon.domain.admin.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean existsByName(String permissionName);
}
