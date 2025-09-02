package com.ballon.domain.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateCategoryRequest {
    @NotBlank(message = "카테고리 이름은 필수 값입니다.")
    String name;

    Long parentId;
}
