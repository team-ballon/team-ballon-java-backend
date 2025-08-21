package com.ballon.domain.admin.repository;

import com.ballon.domain.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long>, CustomAdminRepository {
    Optional<Long> findAdminIdByUser_UserId(Long userId);
}
