package com.ballon.domain.admin.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AdminRequest {
    @NotBlank(message = "이메일은 필수 값입니다.")
    @Email(message = "올바른 이메일 형식을 입력하세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 값입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+]{8,20}$",
            message = "비밀번호는 8~20자, 영문과 숫자를 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "역할 이름은 필수 값입니다.")
    private String roleName;

    @NotEmpty(message = "최소 하나 이상의 권한을 선택해야 합니다.")
    private List<Long> permissionIds;
}
