package com.ballon.domain.keyword.service.impl;

import com.ballon.domain.keyword.dto.PopularKeywordDto;
import com.ballon.domain.keyword.entity.Keyword;
import com.ballon.domain.keyword.repository.KeywordRepository;
import com.ballon.domain.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public void record(String rawKeyword) {
        System.out.println("[KeywordService] record called, raw=" + rawKeyword);

        if (rawKeyword == null) return;
        String display = rawKeyword.trim();
        String normalized = normalize(rawKeyword);
        if (normalized == null || normalized.isEmpty()) return;

        // 이미 있으면 touch
        var opt = repository.findByNormalized(normalized);
        if (opt.isPresent()) {
            opt.get().touch(display);
            return;
        }

        // 없으면 생성 시도 (즉시 flush 해서 여기서 예외가 터지게)
        try {
            repository.saveAndFlush(Keyword.create(display, normalized));
        } catch (DataIntegrityViolationException e) {
            // 경쟁 상태로 유니크 충돌 시 다시 조회해서 touch
            repository.findByNormalized(normalized)
                    .ifPresent(k -> k.touch(display));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<PopularKeywordDto> getTop(Integer limit) {
        return repository.findAllByOrderByCountDescLastSearchedAtDesc(
                        PageRequest.of(0, Math.max(1, limit)))
                .map(k -> new PopularKeywordDto(k.getKeyword(), k.getCount()))
                .getContent();
    }
}
