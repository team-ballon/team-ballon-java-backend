package com.ballon.domain.keyword.controller;

import com.ballon.domain.keyword.dto.PopularKeywordDto;
import com.ballon.domain.keyword.service.KeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keywords")
@RequiredArgsConstructor
@Tag(name = "키워드 API", description = "인기 키워드 및 검색 기록 관련 API")
public class KeywordController {

    private final KeywordService keywordService;

    @GetMapping("/popular")
    @Operation(
            summary = "인기 키워드 조회",
            description = "검색 기록을 기준으로 상위 N개의 인기 키워드를 반환합니다."
    )
    public List<PopularKeywordDto> popular(@RequestParam(defaultValue = "10") Integer limit) {
        return keywordService.getTop(limit);
    }
}