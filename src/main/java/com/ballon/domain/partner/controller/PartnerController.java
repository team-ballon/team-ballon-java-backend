package com.ballon.domain.partner.controller;

import com.ballon.domain.partner.dto.PartnerRegisterRequest;
import com.ballon.domain.partner.dto.PartnerResponse;
import com.ballon.domain.partner.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/partner")
@RequiredArgsConstructor
public class PartnerController {
    private final PartnerService partnerService;

    @PostMapping("/register")
    public ResponseEntity<PartnerResponse> partnerRegister(@Validated @RequestBody PartnerRegisterRequest  partnerRegisterRequest) {
        PartnerResponse partnerResponse = partnerService.partnerRegister(partnerRegisterRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(partnerResponse);
    }
}
