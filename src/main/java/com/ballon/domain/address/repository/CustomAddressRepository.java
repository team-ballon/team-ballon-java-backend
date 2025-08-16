package com.ballon.domain.address.repository;

import com.ballon.domain.address.dto.AddressResponse;

import java.util.List;

public interface CustomAddressRepository {
    List<AddressResponse> findAllByUserId(Long userId);
}
