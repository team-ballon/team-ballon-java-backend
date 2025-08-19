package com.ballon.domain.address.service.impl;

import com.ballon.domain.address.dto.AddressRequest;
import com.ballon.domain.address.dto.AddressResponse;
import com.ballon.domain.address.entity.Address;
import com.ballon.domain.address.repository.AddressRepository;
import com.ballon.domain.address.service.AddressService;
import com.ballon.domain.user.entity.User;
import com.ballon.domain.user.exception.UserNotFoundException;
import com.ballon.domain.user.repository.UserRepository;
import com.ballon.global.common.exception.ForbiddenException;
import com.ballon.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public List<AddressResponse> findAllAddressByUserId(Long userId) {
        return addressRepository.findAllByUserId(userId);
    }

    @Override
    public AddressResponse createAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Address address = Address.of(request, user);
        addressRepository.save(address);

        return new AddressResponse(
                address.getAddressId(),
                address.getRecipient(),
                address.getContactNumber(),
                address.getBaseAddress(),
                address.getDetailAddress()
        );
    }

    @Override
    public void deleteAddress(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("배송지를 찾을 수 없습니다."));

        if(!address.getUser().getUserId().equals(userId)){
            throw new ForbiddenException("인증되지 않은 사용자입니다.");
        }

        addressRepository.delete(address);
    }

    @Override
    public void updateAddress(Long addressId, AddressRequest addressRequest, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("배송지를 찾을 수 없습니다."));

        if(!address.getUser().getUserId().equals(userId)){
            throw new ForbiddenException("인증되지 않은 사용자입니다.");
        }

        address.update(addressRequest);

        addressRepository.save(address);
    }
}
