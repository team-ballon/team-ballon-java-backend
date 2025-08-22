package com.ballon.domain.admin.repository;

import com.ballon.domain.admin.dto.AdminResponse;
import com.ballon.domain.admin.dto.AdminSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomAdminRepository {
    Page<AdminResponse> search(AdminSearchRequest req, Pageable pageable);
}
