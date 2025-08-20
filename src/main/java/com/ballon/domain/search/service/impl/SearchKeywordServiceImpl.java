package com.ballon.domain.search.service.impl;

import com.ballon.domain.search.dto.PopularKeywordDto;
import com.ballon.domain.search.entity.SearchKeyword;
import com.ballon.domain.search.repository.SearchKeywordRepository;
import com.ballon.domain.search.service.SearchKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchKeywordServiceImpl implements SearchKeywordService {

    private final SearchKeywordRepository repository;

    private String normalize(String s) {
        if (s == null) return null;
        return s.trim().toLowerCase(); // 한글도 안전 (대/소문자 영향 없음)
    }

    @Transactional
    @Override
    public void record(String rawKeyword) {
        if (rawKeyword == null) return;
        String display = rawKeyword.trim();
        String normalized = normalize(rawKeyword);
        if (normalized.isEmpty()) return;

        // 1차 시도: 조회 후 touch
        var opt = repository.findByNormalized(normalized);
        if (opt.isPresent()) {
            opt.get().touch(display);
            return;
        }

        //없으면 생성 시도 (경쟁 상태 대비)
        try {
            repository.save(SearchKeyword.create(display, normalized));
        } catch (DataIntegrityViolationException e) {
            // 동시에 들어와 unique 제약 충동 시 재시도
            repository.findByNormalized(normalized)
                    .ifPresent(sk -> sk.touch(display));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<PopularKeywordDto> getTop(Integer limit) {
        return repository.findAllByOrderByCountDescLastSearchedAtDesc(PageRequest.of(0, Math.max(1, limit)))
                .map(sk -> new PopularKeywordDto(sk.getKeyword(), sk.getCount()))
                .getContent();
    }
}
