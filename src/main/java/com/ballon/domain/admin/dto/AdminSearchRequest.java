package com.ballon.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminSearchRequest {
    private String email;
    private String role;
    private List<Long> permissionIds;
    private String sort; // "latest" or "oldest"
}

