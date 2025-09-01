package com.ballon.domain.partner.dto;

import com.ballon.domain.category.dto.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PartnerResponse {
    private Long userId;
    private Long partnerId;
    private String email;
    private String name;
    private String overview;
    private String partnerName;
    private List<CategoryResponse> categories;
}
