package com.ballon.domain.settlement.repository;

import com.ballon.domain.settlement.dto.SettlementSearchRequest;
import com.ballon.domain.settlement.dto.SettlementSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomSettlementRepository {
    Page<SettlementSearchResponse> search(SettlementSearchRequest req, Pageable pageable);
}
