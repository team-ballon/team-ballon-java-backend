package com.ballon.domain.address.repository.impl;

import com.ballon.domain.address.dto.AddressResponse;
import com.ballon.domain.address.entity.QAddress;
import com.ballon.domain.address.repository.CustomAddressRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomAddressRepositoryImpl implements CustomAddressRepository {
    private final JPAQueryFactory queryFactory;

    QAddress address = QAddress.address;

    @Override
    public List<AddressResponse> findAllByUserId(Long userId) {
        return queryFactory
                .select(Projections.constructor(AddressResponse.class,
                        address.addressId,
                        address.name,
                        address.recipient,
                        address.contactNumber,
                        address.baseAddress,
                        address.detailAddress
                ))
                .from(address)
                .where(address.user.userId.eq(userId))
                .orderBy(address.createdAt.desc())
                .fetch();
    }
}
