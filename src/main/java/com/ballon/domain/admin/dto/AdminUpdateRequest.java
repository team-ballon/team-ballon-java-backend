package com.ballon.domain.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AdminUpdateRequest {
    @NotBlank(message = "역할 이름은 필수 값입니다.")
    private String roleName;

    @NotEmpty(message = "최소 하나 이상의 권한을 선택해야 합니다.")
    private List<Long> permissionIds;
}
