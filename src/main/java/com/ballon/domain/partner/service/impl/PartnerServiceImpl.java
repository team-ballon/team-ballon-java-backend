package com.ballon.domain.partner.service.impl;

import com.ballon.domain.category.entity.Category;
import com.ballon.domain.partner.dto.PartnerRegisterRequest;
import com.ballon.domain.partner.dto.PartnerResponse;
import com.ballon.domain.partner.entity.Partner;
import com.ballon.domain.partner.entity.PartnerCategory;
import com.ballon.domain.partner.repository.PartnerCategoryRepository;
import com.ballon.domain.partner.repository.PartnerRepository;
import com.ballon.domain.partner.service.PartnerService;
import com.ballon.domain.user.dto.UserRegisterRequest;
import com.ballon.domain.user.dto.UserResponse;
import com.ballon.domain.user.entity.type.Role;
import com.ballon.domain.user.repository.UserRepository;
import com.ballon.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;
    private final PartnerCategoryRepository partnerCategoryRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public PartnerResponse partnerRegister(PartnerRegisterRequest partnerRegisterRequest) {
        log.info("파트너 등록 요청 수신: {}", partnerRegisterRequest);

        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                partnerRegisterRequest.getEmail(),
                partnerRegisterRequest.getPassword(),
                partnerRegisterRequest.getAge(),
                partnerRegisterRequest.getSex(),
                partnerRegisterRequest.getName()
        );
        log.debug("UserRegisterRequest 생성 완료: {}", userRegisterRequest);

        UserResponse userResponse = userService.registerUser(userRegisterRequest, Role.PARTNER);
        log.info("파트너 계정 생성 완료 - userId: {}", userResponse.getUserId());

        Partner partner = Partner.createPartner(
                partnerRegisterRequest.getPartnerName(),
                partnerRegisterRequest.getOverview(),
                partnerRegisterRequest.getPartnerEmail(),
                userRepository.getReferenceById(userResponse.getUserId())
        );
        partnerRepository.save(partner);
        log.info("파트너 엔티티 저장 완료 - partnerId: {}, partnerName: {}", partner.getPartnerId(), partner.getPartnerName());

        List<PartnerCategory> partnerCategories =
                partnerRegisterRequest.getCategoryIds()
                        .stream()
                        .map(categoryId -> PartnerCategory.of(
                                partner,
                                Category.builder()
                                        .categoryId(categoryId)
                                        .build()
                        ))
                        .toList();
        log.debug("파트너 카테고리 매핑 생성: 총 {}건", partnerCategories.size());

        partnerCategoryRepository.saveAll(partnerCategories);
        log.info("파트너 카테고리 저장 완료");

        PartnerResponse response = new PartnerResponse(
                userResponse.getUserId(),
                partner.getPartnerId(),
                partner.getPartnerEmail(),
                userResponse.getName(),
                partner.getPartnerName(),
                partnerRegisterRequest.getCategoryIds()
        );
        log.info("파트너 등록 처리 완료: {}", response);

        return response;
    }
}
