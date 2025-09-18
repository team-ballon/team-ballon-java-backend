package com.ballon.domain.settlement.service.impl;

import com.ballon.domain.settlement.dto.SettlementSearchRequest;
import com.ballon.domain.settlement.dto.SettlementSearchResponse;
import com.ballon.domain.settlement.repository.SettlementRepository;
import com.ballon.domain.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    private final SettlementRepository settlementRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<SettlementSearchResponse> searchSettlements(SettlementSearchRequest req, Pageable pageable) {
        log.debug("정산 검색 요청: partnerId={}, status={}, 기간={}, page={}",
                req.getPartnerId(), req.getStatus(), req.getStartDate(), pageable);

        Page<SettlementSearchResponse> result = settlementRepository.search(req, pageable);

        log.info("정산 검색 완료: 조회 건수={}, pageNumber={}",
                result.getTotalElements(), pageable.getPageNumber());

        return result;
    }
}
