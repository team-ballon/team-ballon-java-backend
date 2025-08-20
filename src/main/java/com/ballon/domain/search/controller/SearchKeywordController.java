package com.ballon.domain.search.controller;

import com.ballon.domain.search.dto.PopularKeywordDto;
import com.ballon.domain.search.service.SearchKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class SearchKeywordController {

    private final SearchKeywordService Service;

    // 수동 기록 (원하면 사용 아니면 주석처리)
    @PostMapping("/record")
    public void record(@RequestParam String keyword) {
        Service.record(keyword);
    }

    @GetMapping("/popular")
    public List<PopularKeywordDto> popular(@RequestParam(defaultValue = "10") Integer limit) {
        return Service.getTop(limit);
    }
}
