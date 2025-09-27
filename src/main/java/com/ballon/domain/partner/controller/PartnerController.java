package com.ballon.domain.partner.controller;

import com.ballon.domain.partner.dto.PartnerRegisterRequest;
import com.ballon.domain.partner.dto.PartnerResponse;
import com.ballon.domain.partner.service.PartnerService;
import com.ballon.domain.settlement.dto.SettlementSearchRequest;
import com.ballon.domain.settlement.dto.SettlementSearchResponse;
import com.ballon.domain.settlement.service.SettlementService;
import com.ballon.global.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/partner")
@RequiredArgsConstructor
@Tag(name = "입점업체 관리 API", description = "회원가입(입점업체 등록) 관련 API")
public class PartnerController {
    private final PartnerService partnerService;
    private final SettlementService settlementService;

    @Operation(
            summary = "입점업체 회원가입",
            description = "회원 정보, 파트너 정보를 입력받아 새로운 입점업체를 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "입점업체 회원가입 성공",
                            content = @Content(schema = @Schema(implementation = PartnerResponse.class))),
                    @ApiResponse(responseCode = "400", description = "인증이 안된 메일",
                            content = @Content(schema = @Schema(example = "{\"message\": \"이메일 인증이 완료되지 않았습니다.\"}"))),
                    @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일",
                            content = @Content(schema = @Schema(example = "{\"message\": \"이미 가입된 이메일입니다.\"}")))
            }
    )
    @PostMapping("/register")
    public ResponseEntity<PartnerResponse> partnerRegister(@Validated @RequestBody PartnerRegisterRequest  partnerRegisterRequest) {
        PartnerResponse partnerResponse = partnerService.partnerRegister(partnerRegisterRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(partnerResponse);
    }

    @GetMapping("/me/settlement")
    public Page<SettlementSearchResponse> getSettlementMe(Pageable pageable) {
        SettlementSearchRequest request = new SettlementSearchRequest();
        request.setPartnerId(UserUtil.getPartnerId());

        return settlementService.searchSettlements(request, pageable);
    }

    @GetMapping("/{partner-id}")
    public void getPartner(@PathVariable("partner-id") Long partnerId) {

    }
}
