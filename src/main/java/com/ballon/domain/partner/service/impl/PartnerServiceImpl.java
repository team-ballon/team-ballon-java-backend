package com.ballon.domain.partner.service.impl;

import com.ballon.domain.category.dto.CategoryResponse;
import com.ballon.domain.category.entity.Category;
import com.ballon.domain.category.repository.CategoryRepository;
import com.ballon.domain.category.service.CategoryService;
import com.ballon.domain.partner.dto.PartnerRegisterRequest;
import com.ballon.domain.partner.dto.PartnerResponse;
import com.ballon.domain.partner.dto.PartnerSearchRequest;
import com.ballon.domain.partner.dto.UpdatePartnerRequest;
import com.ballon.domain.partner.entity.Partner;
import com.ballon.domain.partner.entity.PartnerCategory;
import com.ballon.domain.partner.repository.PartnerRepository;
import com.ballon.domain.partner.service.PartnerService;
import com.ballon.domain.user.dto.UserRegisterRequest;
import com.ballon.domain.user.dto.UserResponse;
import com.ballon.domain.user.entity.type.Role;
import com.ballon.domain.user.service.UserService;
import com.ballon.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;


    @Override
    public Page<PartnerResponse> searchPartners(PartnerSearchRequest partnerSearchRequest, Pageable pageable) {
        return partnerRepository.search(partnerSearchRequest, pageable);
    }

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
                partnerRegisterRequest.getPartnerEmail()
        );
        partnerRepository.save(partner);
        log.info("파트너 엔티티 저장 완료 - partnerId: {}, partnerName: {}", partner.getPartnerId(), partner.getName());

        PartnerResponse response = new PartnerResponse(
                userResponse.getUserId(),
                partner.getPartnerId(),
                partner.getEmail(),
                userResponse.getName(),
                partner.getOverview(),
                partner.getName(),
                categoryService.assignPartnerCategory(partnerRegisterRequest.getCategoryIds(), partner)
        );
        log.info("파트너 등록 처리 완료: {}", response);

        return response;
    }

    @Override
    public PartnerResponse updatePartner(Long partnerId, UpdatePartnerRequest updatePartnerRequest) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 입점업체입니다."));

        partner.updatePartner(
                updatePartnerRequest.getName(),
                updatePartnerRequest.getOverview(),
                updatePartnerRequest.getEmail()
        );

        partner.getPartnerCategory().removeIf(pc -> true);
        partnerRepository.flush();

        List<Category> categories = categoryRepository.findAllById(updatePartnerRequest.getCategoryIds());
        for(Category category : categories) {
            partner.getPartnerCategory().add(
                    new PartnerCategory(
                            partnerId,
                            partner,
                            category
                    )
            );
        }

        return new PartnerResponse(
                partner.getUser().getUserId(),
                partner.getPartnerId(),
                partner.getEmail(),
                partner.getUser().getName(),
                partner.getName(),
                partner.getOverview(),
                categories.stream()
                        .map(c -> new CategoryResponse(
                        c.getCategoryId(),
                        c.getName()
                )).toList()
        );
    }

    @Override
    public void activePartnerByPartnerId(Long partnerId, Boolean active) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 입점업체입니다."));

        partner.updateActive(active);
    }
}
