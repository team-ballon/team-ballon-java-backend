package com.ballon.domain.partner.service;

import com.ballon.domain.partner.dto.PartnerRegisterRequest;
import com.ballon.domain.partner.dto.PartnerResponse;

public interface PartnerService {
    PartnerResponse partnerRegister(PartnerRegisterRequest partnerRegisterRequest);
}
