package com.ballon.domain.category.service;

import com.ballon.domain.category.dto.CategoryResponse;
import com.ballon.domain.category.dto.CreateCategoryRequest;
import com.ballon.domain.partner.entity.Partner;
import com.ballon.global.cache.CategoryCacheStore;

import java.util.List;

public interface CategoryService {
    CategoryCacheStore.Node createCategory(CreateCategoryRequest createCategoryRequest);

    void deleteCategory(Long id);

    List<CategoryResponse> assignPartnerCategory(List<Long> categoryIds, Partner partner);
}
