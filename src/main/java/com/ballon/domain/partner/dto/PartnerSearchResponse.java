package com.ballon.domain.partner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PartnerSearchResponse {
    private Long partnerId;
    private String partnerName;
    private boolean active;
    private LocalDateTime createdAt;
}
