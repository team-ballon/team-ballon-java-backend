package com.ballon.domain.partner.dto;

import com.ballon.domain.category.dto.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class PartnerResponse {
    private Long userId;
    private Long partnerId;
    private String email;
    private String name;
    private String overview;
    private String partnerName;
    private LocalDateTime createdAt;
    private List<CategoryResponse> categories;
}
