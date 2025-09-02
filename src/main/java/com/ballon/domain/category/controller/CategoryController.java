package com.ballon.domain.category.controller;

import com.ballon.domain.category.dto.CategoryRequest;
import com.ballon.domain.category.service.CategoryService;
import com.ballon.global.cache.CategoryCacheStore;
import com.ballon.global.cache.service.CategoryReadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "카테고리 관리 API", description = "최상위 카테고리, 자식 카테고리, 전체 조회, 트리 구조 조회 API")
public class CategoryController {
    private final CategoryReadService categoryReadService;
    private final CategoryService categoryService;

    @Operation(summary = "전체 카테고리 조회", description = "모든 카테고리를 평면 구조(List)로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(schema = @Schema(implementation = CategoryCacheStore.Node.class)))
    @GetMapping
    public List<CategoryCacheStore.Node> getAll() {
        return categoryReadService.getAll();
    }

    @Operation(summary = "최상위 카테고리 조회", description = "부모가 없는 최상위 카테고리들을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(schema = @Schema(implementation = CategoryCacheStore.Node.class)))
    @GetMapping("/roots")
    public List<CategoryCacheStore.Node> roots() {
        return categoryReadService.getRootCategories();
    }

    @Operation(summary = "자식 카테고리 조회", description = "parentId에 해당하는 자식 카테고리들을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(schema = @Schema(implementation = CategoryCacheStore.Node.class)))
    @GetMapping("/{parent-id}/children")
    public List<CategoryCacheStore.Node> children(
            @Parameter(description = "부모 카테고리 ID", example = "1")
            @PathVariable("parent-id") Long parentId) {
        return categoryReadService.getChildrenOf(parentId);
    }

    @Operation(summary = "이름으로 카테고리 검색", description = "같은 이름을 가진 여러 카테고리를 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(schema = @Schema(implementation = CategoryCacheStore.Node.class)))
    @GetMapping("/search")
    public List<CategoryCacheStore.Node> byName(
            @Parameter(description = "카테고리 이름", example = "스포츠")
            @RequestParam String name) {
        return categoryReadService.getByName(name);
    }

    @Operation(summary = "카테고리 트리 구조 조회", description = "최상위 카테고리부터 하위 카테고리까지 계층 구조로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(schema = @Schema(implementation = CategoryCacheStore.CategoryTree.class)))
    @GetMapping("/tree")
    public List<CategoryCacheStore.CategoryTree> tree() {
        return categoryReadService.getCategoryTree();
    }

    @Operation(summary = "카테고리 생성", description = "parentId와 categoryName을 사용하여서 카테고리 생성을 할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = CategoryCacheStore.Node.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 카테고리")
    })
    @PostMapping
    public ResponseEntity<CategoryCacheStore.Node> createCategory(CategoryRequest categoryRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryRequest));
    }

    @Operation(summary = "카테고리 삭제", description = "categoryId를 사용하여서 카테고리 삭제를 할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공",
                    content = @Content(schema = @Schema(implementation = CategoryCacheStore.Node.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 카테고리")
    })
    @DeleteMapping("/{category-id}")
    public ResponseEntity<Void> createCategory(@PathVariable("category-id") Long categoryId) {
        categoryService.deleteCategory(categoryId);

        return ResponseEntity.noContent().build();
    }
}
