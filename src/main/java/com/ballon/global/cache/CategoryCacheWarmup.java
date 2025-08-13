// src/main/java/com/ballon/global/cache/CategoryCacheWarmup.java
package com.ballon.global.cache;

import com.ballon.domain.category.repository.CategoryRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CategoryCacheWarmup {

    private final CategoryRepository repo;
    private final CategoryCacheStore cache;

    public CategoryCacheWarmup(CategoryRepository repo, CategoryCacheStore cache) {
        this.repo = repo;
        this.cache = cache;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warmup() {
        cache.loadAll(repo.findAllForTree()); // 애플리케이션 시작 시 단 1회 DB 접근
    }
}
