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
        // 부모 카테고리 조회 (parentId가 있으면 참조, 없으면 null)
        Category parent = (createCategoryRequest.getParentId() != null) ? repo.getReferenceById(createCategoryRequest.getParentId()) : null;

        // 새로운 카테고리 엔티티 생성
        Category c = Category.builder()
                .name(createCategoryRequest.getName())
                .parent(parent)
                .build();

        // DB에 저장 후 즉시 flush하여 ID 확보
        repo.saveAndFlush(c);

        // 트랜잭션 커밋 이후 캐시에 반영
        afterCommit(() -> cache.onCreated(
                c.getCategoryId(),
                c.getName(),
                parent == null ? null : parent.getCategoryId()
        ));

        log.info("카테고리 생성 완료: id={}, name={}", c.getCategoryId(), c.getName());

        return cache.getById(c.getCategoryId());
    }

    @Override
    public void deleteCategory(Long id) {
        // 정책: 자식 카테고리까지 함께 삭제 (추가 방어 로직 필요 시 보강 가능)
        repo.deleteById(id);

        // 트랜잭션 커밋 이후 캐시에서 삭제 처리
        afterCommit(() -> cache.onDeleted(id));

        log.info("카테고리 삭제 완료: id={}", id);
    }

    @Override
    public List<CategoryResponse> assignPartnerCategory(List<Long> categoryIds, Partner partner) {
        // 파트너와 카테고리 매핑 엔티티 생성
        List<PartnerCategory> partnerCategories =
                categoryIds.stream()
                        .map(categoryId -> PartnerCategory.of(
                                partner,
                                Category.builder()
                                        .categoryId(categoryId)
                                        .build()
                        ))
                        .toList();

        log.debug("파트너 카테고리 매핑 생성: 총 {}건", partnerCategories.size());

        // DB에 저장
        partnerCategoryRepository.saveAll(partnerCategories);
        log.info("파트너 카테고리 저장 완료");

        // 응답 DTO 변환
        return partnerCategories.stream()
                .map(pc -> new CategoryResponse(
                        pc.getCategory().getCategoryId(),
                        pc.getCategory().getName()
                ))
                .toList();
    }

    private void afterCommit(Runnable r) {
        // 트랜잭션 커밋 이후 실행되는 동작 등록
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() { r.run(); }
        });
    }
}
