package com.ballon.domain.address.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressResponse {
    private Long addressId;
    private String recipient;
    private String contactNumber;
    private String baseAddress;
    private String detailAddress;
}
