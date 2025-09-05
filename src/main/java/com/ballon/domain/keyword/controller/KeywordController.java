package com.ballon.domain.keyword.controller;

import com.ballon.domain.keyword.dto.PopularKeywordDto;
import com.ballon.domain.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// KeywordController
@RestController
@RequestMapping
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;


    @GetMapping("/popular")
    public List<PopularKeywordDto> popular(@RequestParam(defaultValue = "10") Integer limit) {
        return keywordService.getTop(limit);
    }
}
