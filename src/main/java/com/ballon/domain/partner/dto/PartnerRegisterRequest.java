package com.ballon.domain.partner.dto;

import com.ballon.domain.user.entity.type.Sex;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PartnerRegisterRequest {
    // user 등록에 필요한 컬럼
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+]{8,20}$",
            message = "비밀번호는 8~20자, 영문과 숫자를 포함해야 합니다."
    )
    private String password;

    @NotNull(message = "나이는 필수입니다.")
    @Min(value = 1, message = "나이는 1 이상이어야 합니다.")
    private Integer age;

    @NotNull(message = "성별은 필수입니다.")
    private Sex sex;

    private String name;

    // partner 등록에 필요한 컬럼
    @NotBlank(message = "업체명은 필수입니다.")
    @Size(max = 100, message = "업체명은 최대 100자까지 가능합니다.")
    private String partnerName;

    private String overview;

    @NotBlank(message = "업체 이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String partnerEmail;

    @NotEmpty(message = "최소 한 개 이상의 카테고리를 선택해야 합니다.")
    private List<Long> categoryIds;
}
