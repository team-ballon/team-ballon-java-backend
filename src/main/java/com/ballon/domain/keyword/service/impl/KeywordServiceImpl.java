package com.ballon.domain.keyword.service.impl;

import com.ballon.domain.keyword.dto.PopularKeywordDto;
import com.ballon.domain.keyword.entity.Keyword;
import com.ballon.domain.keyword.repository.KeywordRepository;
import com.ballon.domain.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KeywordServiceImpl implements KeywordService {

    private final KeywordRepository repository;

    private String normalize(String s) {
        if (s == null) return null;
        return s.trim().toLowerCase();
    }

    @Override
    public void saveKeyword(String rawKeyword) {
        if (rawKeyword == null) {
            log.debug("키워드 저장 요청 무시됨: 입력값이 null");
            return;
        }

        String display = rawKeyword.trim();
        String normalized = normalize(rawKeyword);
        if (normalized.isEmpty()) {
            log.debug("키워드 저장 요청 무시됨: 공백 문자열 입력");
            return;
        }

        log.info("키워드 저장 시도 - display: '{}', normalized: '{}'", display, normalized);

        // 이미 존재하면 touch
        var opt = repository.findByNormalized(normalized);
        if (opt.isPresent()) {
            opt.get().touch(display);
            log.info("기존 키워드 업데이트 완료 - display: '{}', count: {}",
                    display, opt.get().getCount());
            return;
        }

        // 없으면 신규 생성 시도
        try {
            repository.saveAndFlush(Keyword.create(display, normalized));
            log.info("신규 키워드 저장 완료 - display: '{}'", display);
        } catch (DataIntegrityViolationException e) {
            // 경쟁 상태로 유니크 충돌 시 다시 조회해서 touch
            repository.findByNormalized(normalized)
                    .ifPresent(k -> {
                        k.touch(display);
                        log.warn("경쟁 상태 감지: 기존 키워드 업데이트 처리 - display: '{}', count: {}",
                                display, k.getCount());
                    });
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<PopularKeywordDto> getTop(Integer limit) {
        int fetchLimit = Math.max(1, limit);
        log.debug("인기 키워드 조회 요청 - limit: {}", fetchLimit);

        List<PopularKeywordDto> result = repository.findAllByOrderByCountDescLastSearchedAtDesc(
                        PageRequest.of(0, fetchLimit))
                .map(k -> new PopularKeywordDto(k.getKeyword(), k.getCount()))
                .getContent();

        log.info("인기 키워드 조회 완료 - 조회된 개수: {}", result.size());
        return result;
    }
}
