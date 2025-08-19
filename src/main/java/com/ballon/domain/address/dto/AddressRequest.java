package com.ballon.domain.address.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressRequest {
    private String name;
    private String recipient;
    private String contactNumber;
    private String baseAddress;
    private String detailAddress;
}
