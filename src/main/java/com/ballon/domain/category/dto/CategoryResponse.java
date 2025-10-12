package com.ballon.domain.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class CategoryResponse {
    private Long  categoryId;
    private String categoryName;
}
