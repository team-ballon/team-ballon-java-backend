package com.ballon.domain.search.repository;

import com.ballon.domain.search.entity.SearchKeyword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, Long> {

    Optional<SearchKeyword> findByNormalized(String normalized);

    // count DESC, Last_searched_at DESC 정렬을 pageable 로 처리
    Page<SearchKeyword> findAllByOrderByCountDescLastSearchedAtDesc(Pageable pageable);
}
