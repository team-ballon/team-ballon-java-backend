package com.ballon.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PermissionResponse {
    private long permissionId;
    private String permissionName;
    private String permissionDescription;
}
