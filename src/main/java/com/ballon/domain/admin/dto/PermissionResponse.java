package com.ballon.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PermissionResponse {
    private long permissionId;
    private String permissionName;
    private String permissionDescription;
}
