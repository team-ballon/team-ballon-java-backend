package com.ballon.global.cache.service;

import com.ballon.global.cache.CategoryCacheStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryReadService {

    private final CategoryCacheStore cache;

    public CategoryReadService(CategoryCacheStore cache) {
        this.cache = cache;
    }

    // 최상위 카테고리 리스트
    public List<CategoryCacheStore.Node> getRootCategories() {
        return cache.getRoots();
    }

    // parentId의 자식 리스트
    public List<CategoryCacheStore.Node> getChildrenOf(Long parentId) {
        return cache.getChildrenOf(parentId);
    }

    // 단건 조회(필요 시)
    public CategoryCacheStore.Node getById(Long id) {
        return cache.getById(id);
    }
}
