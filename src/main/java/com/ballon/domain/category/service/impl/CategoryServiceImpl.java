package com.ballon.domain.category.service.impl;

import com.ballon.domain.category.dto.CategoryResponse;
import com.ballon.domain.category.dto.CreateCategoryRequest;
import com.ballon.domain.category.entity.Category;
import com.ballon.domain.category.repository.CategoryRepository;
import com.ballon.domain.category.service.CategoryService;
import com.ballon.domain.partner.entity.Partner;
import com.ballon.domain.partner.entity.PartnerCategory;
import com.ballon.domain.partner.repository.PartnerCategoryRepository;
import com.ballon.global.cache.CategoryCacheStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;
    private final CategoryCacheStore cache;
    private final PartnerCategoryRepository partnerCategoryRepository;

    @Override
    public CategoryCacheStore.Node createCategory(CreateCategoryRequest createCategoryRequest) {
        Category parent = (createCategoryRequest.getParentId() != null) ? repo.getReferenceById(createCategoryRequest.getParentId()) : null;
        Category c = Category.builder().name(createCategoryRequest.getName()).parent(parent).build();
        repo.saveAndFlush(c); // ID 확보

        // 커밋 후 캐시에 반영
        afterCommit(() -> cache.onCreated(c.getCategoryId(), c.getName(),
                parent == null ? null : parent.getCategoryId()));
        return cache.getById(c.getCategoryId());
    }

    @Override
    public void deleteCategory(Long id) {
        // 정책: 자식까지 함께 삭제(필요 시 방어 로직 추가)
        repo.deleteById(id);

        afterCommit(() -> cache.onDeleted(id));
    }

    @Override
    public List<CategoryResponse> assignPartnerCategory(List<Long> categoryIds, Partner partner) {
        List<PartnerCategory> partnerCategories =
                categoryIds
                        .stream()
                        .map(categoryId -> PartnerCategory.of(
                                partner,
                                Category.builder()
                                        .categoryId(categoryId)
                                        .build()
                        ))
                        .toList();
        log.debug("파트너 카테고리 매핑 생성: 총 {}건", partnerCategories.size());

        partnerCategoryRepository.saveAll(partnerCategories);
        log.info("파트너 카테고리 저장 완료");

        return partnerCategories.stream()
                .map(pc -> new CategoryResponse(
                        pc.getCategory().getCategoryId(),
                        pc.getCategory().getName()
                )).toList();
    }

    private void afterCommit(Runnable r) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { r.run(); }
        });
    }
}