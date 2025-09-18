package com.ballon.domain.settlement.service;

import com.ballon.domain.settlement.dto.SettlementSearchRequest;
import com.ballon.domain.settlement.dto.SettlementSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SettlementService {
    Page<SettlementSearchResponse> searchSettlements(SettlementSearchRequest req, Pageable pageable);
}
