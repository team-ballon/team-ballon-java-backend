package com.ballon.domain.keyword.service;

import com.ballon.domain.keyword.dto.PopularKeywordDto;

import java.util.List;

public interface KeywordService {
    void record(String rawKeyword);
    List<PopularKeywordDto> getTop(Integer limit);
}
