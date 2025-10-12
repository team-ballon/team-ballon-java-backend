package com.ballon.domain.address.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class AddressResponse {
    private Long addressId;
    private String name;
    private String recipient;
    private String contactNumber;
    private String baseAddress;
    private String detailAddress;
}
