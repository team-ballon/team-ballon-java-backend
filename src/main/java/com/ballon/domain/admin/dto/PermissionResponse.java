package com.ballon.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class PermissionResponse {
    private long permissionId;
    private String permissionName;
    private String permissionDescription;
}
