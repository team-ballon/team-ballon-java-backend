package com.ballon.domain.settlement.service;

import com.ballon.domain.partner.entity.Partner;
import com.ballon.domain.partner.repository.PartnerRepository;
import com.ballon.domain.settlement.entity.Settlement;
import com.ballon.domain.settlement.entity.type.SettlementStatus;
import com.ballon.domain.settlement.repository.OrderSettlementRepository;
import com.ballon.domain.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementSchedulerService {

    private final PartnerRepository partnerRepository;
    private final SettlementRepository settlementRepository;
    private final OrderSettlementRepository orderSettlementRepository;

    /**
     * 매월 1일 00시 실행
     * 직전 달 기간 정산 자동 생성
     */
    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void generateMonthlySettlements() {
        LocalDate today = LocalDate.now();
        LocalDate periodStart = today.minusMonths(1).withDayOfMonth(1);
        LocalDate periodEnd = today.minusMonths(1).withDayOfMonth(today.minusMonths(1).lengthOfMonth());

        log.info("자동 정산 시작: {} ~ {}", periodStart, periodEnd);

        List<Partner> partners = partnerRepository.findAll();

        for (Partner partner : partners) {
            // 주문 금액 합계 조회
            Integer totalAmount = orderSettlementRepository.sumPartnerSales(partner.getPartnerId(), periodStart, periodEnd);

            if (totalAmount == null || totalAmount == 0) {
                log.debug("파트너 {}({}) 기간 내 주문 없음, 정산 생성 생략", partner.getPartnerId(), partner.getPartnerName());
                continue;
            }

            // 중복 체크 (이미 생성된 정산 있으면 skip)
            boolean exists = settlementRepository.existsByPartnerAndPeriodStartAndPeriodEnd(partner, periodStart, periodEnd);
            if (exists) {
                log.warn("파트너 {}({}) 기간 {} ~ {} 이미 정산 생성됨, skip",
                        partner.getPartnerId(), partner.getPartnerName(), periodStart, periodEnd);
                continue;
            }

            Settlement settlement = Settlement.builder()
                    .partner(partner)
                    .periodStart(periodStart)
                    .periodEnd(periodEnd)
                    .totalAmount(totalAmount)
                    .status(SettlementStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            settlementRepository.save(settlement);

            log.info("파트너 {}({}) 정산 생성 완료: {}원", partner.getPartnerId(), partner.getPartnerName(), totalAmount);
        }

        log.info("자동 정산 완료: {} ~ {}", periodStart, periodEnd);
    }
}
