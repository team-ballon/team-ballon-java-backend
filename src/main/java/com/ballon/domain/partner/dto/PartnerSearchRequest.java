package com.ballon.domain.partner.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PartnerSearchRequest {
    private String name;         // 업체명 검색
    private String email;        // 이메일 검색
    private Boolean active;      // 활성 상태
    private List<Long> categoryIds; // 카테고리 ID 리스트
    private String sort;         // 정렬 기준
}