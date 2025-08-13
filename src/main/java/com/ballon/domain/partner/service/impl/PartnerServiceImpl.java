package com.ballon.domain.partner.service.impl;

import com.ballon.domain.partner.dto.PartnerRegisterRequest;
import com.ballon.domain.partner.dto.PartnerResponse;
import com.ballon.domain.partner.repository.PartnerCategoryRepository;
import com.ballon.domain.partner.repository.PartnerRepository;
import com.ballon.domain.partner.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {
    private final PartnerRepository partnerRepository;
    private final PartnerCategoryRepository partnerCategoryRepository;

    @Override
    public PartnerResponse partnerRegister(PartnerRegisterRequest partnerRegisterRequest) {
        return null;
    }
}
