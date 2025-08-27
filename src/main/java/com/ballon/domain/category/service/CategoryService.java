package com.ballon.domain.category.service;

import com.ballon.domain.category.dto.CategoryRequest;
import com.ballon.global.cache.CategoryCacheStore;

public interface CategoryService {
    CategoryCacheStore.Node createCategory(CategoryRequest categoryRequest);

    void deleteCategory(Long categoryId);
}