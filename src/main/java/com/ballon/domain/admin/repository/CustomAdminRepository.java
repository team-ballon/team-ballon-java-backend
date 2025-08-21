package com.ballon.domain.admin.repository;

import com.ballon.domain.admin.dto.AdminSearchRequest;
import com.ballon.domain.admin.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomAdminRepository {
    Page<Admin> search(AdminSearchRequest req, Pageable pageable);
}
