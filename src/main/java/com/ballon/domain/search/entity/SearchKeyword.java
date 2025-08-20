package com.ballon.domain.search.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_keyword",
       uniqueConstraints = @UniqueConstraint(name = "uk_search_keyword_normalized", columnNames = "normalized"),
       indexes = {
            @Index(name = "idx_search_keyword_count", columnList = "count"),
            @Index(name = "idx_search_keyword_last", columnList = "last_searched_at")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SearchKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 화면에 보여줄 "최근 표기"를 보관
    @Column(length = 200, nullable = false)
    private String keyword;

    // 검색어 정규화(소문자 + trim). 유니크
    @Column(length = 200, nullable = false)
    private String normalized;

    @Column(nullable = false)
    private Long count;

    @Column(name = "last_searched_at", nullable = false)
    private LocalDateTime lastSearchedAt;

    // 검색 기록 반영
    public void touch(String latestDisplay) {
        this.keyword = latestDisplay;
        this.count = this.count + 1;
        this.lastSearchedAt = LocalDateTime.now();
    }

    // 최초 생성 팩토리
    public static SearchKeyword create(String display, String normalized) {
        return SearchKeyword.builder()
                .keyword(display)
                .normalized(normalized)
                .count(1L)
                .lastSearchedAt(LocalDateTime.now())
                .build();
    }
}
