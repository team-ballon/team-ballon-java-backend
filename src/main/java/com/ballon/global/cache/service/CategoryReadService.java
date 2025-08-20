package com.ballon.global.cache.service;

import com.ballon.global.cache.CategoryCacheStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryReadService {

    private final CategoryCacheStore cache;

    public List<CategoryCacheStore.Node> getAll() {
        return cache.getAll();
    }

    public List<CategoryCacheStore.Node> getRootCategories() {
        return cache.getRoots();
    }

    public List<CategoryCacheStore.Node> getChildrenOf(Long parentId) {
        return cache.getChildrenOf(parentId);
    }

    public CategoryCacheStore.Node getById(Long id) {
        return cache.getById(id);
    }

    // 같은 이름 여러 개 가능하므로 리스트 반환
    public List<CategoryCacheStore.Node> getByName(String name) {
        return cache.getByName(name);
    }

    public List<CategoryCacheStore.CategoryTree> getCategoryTree() {
        return cache.getCategoryTree();
    }
}
