package com.ballon.domain.product.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProductSearchRequest {
    private String name;
    private Integer minPrice;
    private Integer maxPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long categoryId;
    private Long partnerId;
    private String sort; // "oldest", "name", "price_low", "price_high"
}
