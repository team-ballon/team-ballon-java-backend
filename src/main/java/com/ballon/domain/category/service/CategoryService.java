package com.ballon.domain.category.service;

public interface CategoryService {
    Long createCategory(String name, Long parentId);

    void renameCategory(Long id, String newName);

    void deleteCategory(Long id);
}
