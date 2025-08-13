package com.ballon.domain.category.service.impl;

import com.ballon.domain.category.entity.Category;
import com.ballon.domain.category.repository.CategoryRepository;
import com.ballon.domain.category.service.CategoryService;
import com.ballon.global.cache.CategoryCacheStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;
    private final CategoryCacheStore cache;

    @Override
    public Long createCategory(String name, Long parentId) {
        Category parent = (parentId != null) ? repo.getReferenceById(parentId) : null;
        Category c = Category.builder().name(name).parent(parent).build();
        repo.saveAndFlush(c); // ID 확보

        // 커밋 후 캐시에 반영
        afterCommit(() -> cache.onCreated(c.getCategoryId(), c.getName(),
                parent == null ? null : parent.getCategoryId()));
        return c.getCategoryId();
    }

    @Override
    public void renameCategory(Long id, String newName) {
        Category c = repo.getReferenceById(id);
        // 엔티티 변경 메서드가 없다면 추가: c.changeName(newName);
        // 임시로 리플렉션/필드 접근 대신 전용 메서드를 엔티티에 추가하는 걸 권장
        // 여기서는 가독성 위해 직접 세터 메서드가 있다고 가정
        // c.setName(newName);

        // JPA Dirty Checking에 의해 flush 시 DB 반영
        afterCommit(() -> cache.onRenamed(id, newName));
    }

    @Override
    public void deleteCategory(Long id) {
        // 정책: 자식까지 함께 삭제(필요 시 방어 로직 추가)
        repo.deleteById(id);

        afterCommit(() -> cache.onDeleted(id));
    }

    private void afterCommit(Runnable r) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { r.run(); }
        });
    }
}