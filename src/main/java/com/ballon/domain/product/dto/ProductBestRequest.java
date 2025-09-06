package com.ballon.domain.product.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductBestRequest {
    private Long categoryId;
    private Long partnerId;
}
