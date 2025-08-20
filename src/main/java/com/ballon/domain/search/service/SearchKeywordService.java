package com.ballon.domain.search.service;

import com.ballon.domain.search.dto.PopularKeywordDto;

import java.util.List;

public interface SearchKeywordService {
    void record(String rawKeyword);
    List<PopularKeywordDto> getTop(Integer limit);
}
