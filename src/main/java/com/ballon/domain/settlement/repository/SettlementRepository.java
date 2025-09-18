package com.ballon.domain.settlement.repository;

import com.ballon.domain.partner.entity.Partner;
import com.ballon.domain.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface SettlementRepository extends JpaRepository<Settlement, Long>, CustomSettlementRepository {
    boolean existsByPartnerAndPeriodStartAndPeriodEnd(Partner partner, LocalDate periodStart, LocalDate periodEnd);
}
