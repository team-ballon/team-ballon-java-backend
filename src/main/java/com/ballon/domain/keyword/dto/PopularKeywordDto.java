package com.ballon.domain.keyword.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PopularKeywordDto {
    private String keyword; // 화면 표기
    private long count;
}