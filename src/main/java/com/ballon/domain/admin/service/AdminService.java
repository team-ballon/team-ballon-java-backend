package com.ballon.domain.admin.service;

import com.ballon.domain.admin.dto.AdminRequest;
import com.ballon.domain.admin.dto.AdminResponse;

public interface AdminService {
    AdminResponse createAdmin(AdminRequest adminRequest);
}
