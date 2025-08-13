package com.ballon.domain.category.controller;

import com.ballon.global.cache.CategoryCacheStore;
import com.ballon.global.cache.service.CategoryReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryReadService categoryReadService;

    @GetMapping("/roots")
    public List<CategoryCacheStore.Node> roots() {
        return categoryReadService.getRootCategories(); // 캐시에서 반환
    }

    @GetMapping("/{parentId}/children")
    public List<CategoryCacheStore.Node> children(@PathVariable Long parentId) {
        return categoryReadService.getChildrenOf(parentId); // 캐시에서 반환
    }
}