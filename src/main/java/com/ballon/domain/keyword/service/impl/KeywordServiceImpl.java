package com.ballon.domain.keyword.service.impl;

import com.ballon.domain.keyword.dto.PopularKeywordDto;
import com.ballon.domain.keyword.entity.Keyword;
import com.ballon.domain.keyword.repository.KeywordRepository;
import com.ballon.domain.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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

        try {
            repository.saveAndFlush(Keyword.create(display, normalized));
            log.info("신규 키워드 저장 완료 - display: '{}'", display);
        } catch (DataIntegrityViolationException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ConstraintViolationException cve
                    && Objects.equals(cve.getConstraintName(), "uk_keyword_normalized")) {
                // 유니크 충돌만 처리
                repository.findByNormalized(normalized)
                        .ifPresent(k -> k.touch(display));
            } else {
                throw e; // 다른 제약 위반은 그대로 전파
            }
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
