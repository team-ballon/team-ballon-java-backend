package com.ballon.domain.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordUpdateRequest {

    private String currentPassword;

    private String newPassword;
}
