package com.ballon.domain.partner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerResponse {
    private Long userId;
    private Long partnerId;
    private String email;
    private String name;
    private String partnerName;
    private List<Long> categoryIds;
}
