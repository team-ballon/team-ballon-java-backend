package com.ballon.domain.address.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressRequest {

    @NotBlank(message = "배송지명은 필수입니다.")
    @Size(max = 100, message = "배송지명은 최대 10자까지 가능합니다.")
    private String name;

    @NotBlank(message = "수령인은 필수입니다.")
    @Size(max = 50, message = "수령인은 최대 50자까지 가능합니다.")
    private String recipient;

    @NotBlank(message = "연락처는 필수입니다.")
    @Pattern(
            regexp = "^(01[0-9])-?([0-9]{3,4})-?([0-9]{4})$",
            message = "연락처는 010-1234-5678 형식이어야 합니다."
    )
    private String contactNumber;

    @NotBlank(message = "주소는 필수입니다.")
    @Size(max = 255, message = "주소는 최대 255자까지 가능합니다.")
    private String baseAddress;

    @NotBlank(message = "상세 주소는 필수입니다.")
    @Size(max = 255, message = "상세 주소는 최대 255자까지 가능합니다.")
    private String detailAddress;
}
