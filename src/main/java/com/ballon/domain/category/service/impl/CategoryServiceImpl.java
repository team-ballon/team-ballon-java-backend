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
import com.ballon.global.common.exception.ConflictException;
import com.ballon.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryCacheStore cache;
    private final PartnerCategoryRepository partnerCategoryRepository;

    @Override
    public CategoryCacheStore.Node createCategory(CreateCategoryRequest createCategoryRequest) {
        Category parent;
        Long parentCategoryId = createCategoryRequest.getParentId();

        // 1. 부모 카테고리 존재 여부 검증
        if(Objects.nonNull(parentCategoryId)) {
            if(categoryRepository.existsById(parentCategoryId)) {
                parent = categoryRepository.getReferenceById(parentCategoryId);
            } else {
                throw new NotFoundException("존재하지 않는 부모 카테고리입니다.");
            }
        } else {
            parent = null;
        }

        // 2. 새 카테고리 생성
        Category category = Category.builder()
                .name(createCategoryRequest.getName())
                .parent(parent)
                .build();

        // DB에 저장 후 즉시 flush하여 ID 확보
        try {
            // 3. DB 저장 및 즉시 flush (유니크 제약 검사 포함)
            categoryRepository.saveAndFlush(category);
        } catch (DataIntegrityViolationException e) {
            // 4. 복합 유니크 제약(name, parent_id) 위반 시 명확한 예외 반환
            throw new ConflictException("이미 존재하는 카테고리입니다: name=" + createCategoryRequest.getName());
        }

        // 트랜잭션 커밋 이후 캐시에 반영
        afterCommit(() -> cache.onCreated(
                category.getCategoryId(),
                category.getName(),
                parent == null ? null : parentCategoryId
        ));

        log.info("카테고리 생성 완료: id={}, name={}", category.getCategoryId(), category.getName());

        return cache.getById(category.getCategoryId());
    }

    @Override
    public void deleteCategory(Long categoryId) {
        // 존재하지 않을 경우 GlobalExceptionHandler에서 EmptyResultDataAccessException 처리
        categoryRepository.deleteById(categoryId);

        // 트랜잭션 커밋 이후 캐시에서 삭제 처리
        afterCommit(() -> cache.onDeleted(categoryId));

        log.info("카테고리 삭제 완료: id={}", categoryId);
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
