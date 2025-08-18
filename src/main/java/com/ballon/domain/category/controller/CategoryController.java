package com.ballon.domain.category.controller;

import com.ballon.global.cache.CategoryCacheStore;
import com.ballon.global.cache.service.CategoryReadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "카테고리 관리 API", description = "최상위 카테고리, 자식 카테고리 관련 API")
public class CategoryController {
    private final CategoryReadService categoryReadService;

    @GetMapping
    public List<CategoryCacheStore.Node> getAll() {
        return categoryReadService.getAll();
    }

    @GetMapping("/roots")
    public List<CategoryCacheStore.Node> roots() {
        return categoryReadService.getRootCategories(); // 캐시에서 반환
    }

    @GetMapping("/{parentId}/children")
    public List<CategoryCacheStore.Node> children(@PathVariable Long parentId) {
        return categoryReadService.getChildrenOf(parentId); // 캐시에서 반환
    }
}