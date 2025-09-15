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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public List<AddressResponse> findAllAddressByUserId(Long userId) {
        log.debug("사용자 주소 전체 조회 요청: userId={}", userId);
        List<AddressResponse> addresses = addressRepository.findAllByUserId(userId);
        log.info("사용자 주소 조회 완료: userId={}, 조회 건수={}", userId, addresses.size());
        return addresses;
    }

    @Override
    public AddressResponse createAddress(Long userId, AddressRequest request) {
        log.debug("주소 생성 요청: userId={}, request={}", userId, request);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Address address = Address.of(request, user);
        addressRepository.save(address);

        log.info("주소 생성 완료: addressId={}, userId={}", address.getAddressId(), userId);

        return new AddressResponse(
                address.getAddressId(),
                address.getName(),
                address.getRecipient(),
                address.getContactNumber(),
                address.getBaseAddress(),
                address.getDetailAddress()
        );
    }

    @Override
    public void deleteAddress(Long addressId, Long userId) {
        log.debug("주소 삭제 요청: addressId={}, userId={}", addressId, userId);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("배송지를 찾을 수 없습니다."));

        if (!address.getUser().getUserId().equals(userId)) {
            log.warn("주소 삭제 실패 - 권한 없음: addressId={}, 요청자 userId={}", addressId, userId);
            throw new ForbiddenException("인증되지 않은 사용자입니다.");
        }

        addressRepository.delete(address);
        log.info("주소 삭제 완료: addressId={}, userId={}", addressId, userId);
    }

    @Override
    public void updateAddress(Long addressId, AddressRequest addressRequest, Long userId) {
        log.debug("주소 수정 요청: addressId={}, userId={}, request={}", addressId, userId, addressRequest);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("배송지를 찾을 수 없습니다."));

        if (!address.getUser().getUserId().equals(userId)) {
            log.warn("주소 수정 실패 - 권한 없음: addressId={}, 요청자 userId={}", addressId, userId);
            throw new ForbiddenException("인증되지 않은 사용자입니다.");
        }

        address.update(addressRequest);
        addressRepository.save(address);

        log.info("주소 수정 완료: addressId={}, userId={}", addressId, userId);
    }
}
