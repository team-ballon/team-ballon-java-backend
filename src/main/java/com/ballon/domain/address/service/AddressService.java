package com.ballon.domain.address.service;

import com.ballon.domain.address.dto.AddressRequest;
import com.ballon.domain.address.dto.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> findAllAddressByUserId(Long userId);

    AddressResponse createAddress(Long userId, AddressRequest request);

    void deleteAddress(Long addressId, Long userId);

    void updateAddress(Long addressId, AddressRequest addressRequest, Long userId);
}
