package com.ballon.domain.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponse {
    private Long  categoryId;
    private String categoryName;
}
