package com.ballon.domain.admin.repository;

import com.ballon.domain.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long>, CustomAdminRepository {
    @Query("SELECT a.adminId FROM Admin a WHERE a.user.userId = :userId")
    Optional<Long> findAdminIdByUserId(Long userId);
}
