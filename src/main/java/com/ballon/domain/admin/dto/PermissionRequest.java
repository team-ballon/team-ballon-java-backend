package com.ballon.domain.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PermissionRequest {
    private String permissionName;
    private String permissionDescription;
}
