package com.ballon.domain.admin.service;

import com.ballon.domain.admin.dto.AdminRequest;
import com.ballon.domain.admin.dto.AdminResponse;
import com.ballon.domain.admin.dto.AdminSearchRequest;
import com.ballon.domain.admin.dto.AdminUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    AdminResponse getAdminByAdminId(Long adminId);

    Page<AdminResponse> searchAdmins(AdminSearchRequest req, Pageable pageable);

    AdminResponse createAdmin(AdminRequest adminRequest);

    AdminResponse updateAdmin(Long adminId, AdminUpdateRequest adminUpdateRequest);

    void removeAdminByAdminId(Long adminId);
}
