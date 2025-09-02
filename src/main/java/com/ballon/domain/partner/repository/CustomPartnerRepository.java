package com.ballon.domain.partner.repository;

import com.ballon.domain.partner.dto.PartnerSearchRequest;
import com.ballon.domain.partner.dto.PartnerSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPartnerRepository {
    Page<PartnerSearchResponse> search(PartnerSearchRequest req, Pageable pageable);
}
