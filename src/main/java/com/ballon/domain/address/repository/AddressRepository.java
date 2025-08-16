package com.ballon.domain.address.repository;

import com.ballon.domain.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long>, CustomAddressRepository {
}
