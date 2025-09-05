package com.ballon.domain.keyword.repository;

import com.ballon.domain.keyword.entity.Keyword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Optional<Keyword> findByNormalized(String normalized);

    // count DESC, Last_searched_at DESC 정렬을 pageable 로 처리
    Page<Keyword> findAllByOrderByCountDescLastSearchedAtDesc(Pageable pageable);
}
