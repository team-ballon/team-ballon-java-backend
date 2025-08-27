package com.ballon.domain.category.service.impl;

import com.ballon.domain.category.dto.CategoryRequest;
import com.ballon.domain.category.entity.Category;
import com.ballon.domain.category.repository.CategoryRepository;
import com.ballon.domain.category.service.CategoryService;
import com.ballon.global.cache.CategoryCacheStore;
import com.ballon.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryCacheStore categoryCacheStore;

    @Override
    public CategoryCacheStore.Node createCategory(CategoryRequest categoryRequest) {
        Category parent;
        if (categoryRequest.getParentId() != null) {
            parent = categoryRepository.findById(categoryRequest.getParentId())
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 부모 카테고리입니다."));
        } else {
            parent = null;
        }

        Category category = Category.builder().name(categoryRequest.getCategoryName()).parent(parent).build();
        categoryRepository.save(category);

        // 커밋 후 캐시에 반영
        afterCommit(() -> categoryCacheStore.onCreated(category.getCategoryId(), category.getName(),
                parent == null ? null : parent.getCategoryId()));
        return categoryCacheStore.getById(category.getCategoryId());
    }

    @Override
    public void deleteCategory(Long categoryId) {
        if(!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("존재하지 않는 카테고리입니다.");
        }

        categoryRepository.deleteById(categoryId);

        afterCommit(() -> categoryCacheStore.onDeleted(categoryId));
    }

    private void afterCommit(Runnable r) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { r.run(); }
        });
    }
}