package com.ballon.domain.partner.repository;

import com.ballon.domain.partner.dto.PartnerResponse;
import com.ballon.domain.partner.dto.PartnerSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPartnerRepository {
    Page<PartnerResponse> search(PartnerSearchRequest req, Pageable pageable);
}
