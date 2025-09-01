package com.ballon.domain.partner.repository;

import com.ballon.domain.partner.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, Long>, CustomPartnerRepository {
    @Query("SELECT p.partnerId FROM Partner p WHERE p.user.userId = :userId")
    Optional<Long> findPartnerIdByUserId(@Param("userId") Long userId);

    Boolean existsByEmail(String email);

    Optional<Long> findPartnerIdByUser_UserId(Long userId);
}
