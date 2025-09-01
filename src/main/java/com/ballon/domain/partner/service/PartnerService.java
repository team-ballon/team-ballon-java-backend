package com.ballon.domain.partner.service;

import com.ballon.domain.partner.dto.PartnerRegisterRequest;
import com.ballon.domain.partner.dto.PartnerResponse;
import com.ballon.domain.partner.dto.PartnerSearchRequest;
import com.ballon.domain.partner.dto.UpdatePartnerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PartnerService {
    PartnerResponse partnerRegister(PartnerRegisterRequest partnerRegisterRequest);

    void activePartnerByPartnerId(Long partnerId, Boolean active);

    PartnerResponse updatePartner(Long partnerId, UpdatePartnerRequest updatePartnerRequest);

    Page<PartnerResponse> searchPartners(PartnerSearchRequest partnerSearchRequest, Pageable pageable);
}
