package com.ballon.domain.admin.repository;

import com.ballon.domain.admin.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean existsByName(String permissionName);

    Optional<Permission> findByName(String permissionName);
}
