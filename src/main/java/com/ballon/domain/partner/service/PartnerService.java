package com.ballon.domain.partner.service;

import com.ballon.domain.partner.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PartnerService {
    PartnerResponse getPartnerByPartnerId(Long partnerId);

    PartnerResponse partnerRegister(PartnerRegisterRequest partnerRegisterRequest);

    void activePartnerByPartnerId(Long partnerId, Boolean active);

    PartnerResponse updatePartner(Long partnerId, UpdatePartnerRequest updatePartnerRequest);

    Page<PartnerSearchResponse> searchPartners(PartnerSearchRequest partnerSearchRequest, Pageable pageable);
}
